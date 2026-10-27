from django.test import TestCase
from rest_framework.test import APIClient
from rest_framework import status
from apps.authentication.models import User, Department

class UserManagementAPITest(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.dept = Department.objects.create(name="综合部", code="ZH_DEPT")
        self.admin_user = User.objects.create_superuser(
            username="admin_test",
            password="password123",
            user_code="99999999"
        )
        self.client.force_authenticate(user=self.admin_user)

    def test_create_user_and_unique_user_code(self):
        # 1. 创建人员
        payload = {
            "user_code": "200000073",
            "real_name": "分析部-孙金明",
            "department": str(self.dept.id),
            "company": "湖北九控检测技术有限公司",
            "gender": "male",
            "phone": "13800000001",
            "email": "sun@example.com"
        }
        res = self.client.post("/api/auth/users/", payload, format="json")
        self.assertEqual(res.status_code, status.HTTP_201_CREATED)
        self.assertTrue(User.objects.filter(user_code="200000073").exists())

        # 2. 校验人员编码重复
        duplicate_res = self.client.post("/api/auth/users/", payload, format="json")
        self.assertEqual(duplicate_res.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn("已存在", str(duplicate_res.data))

    def test_list_and_search_user(self):
        User.objects.create(
            username="user_test_1",
            user_code="200000070",
            real_name="副经理-石浩然",
            company="湖北九控检测技术有限公司",
            department=self.dept
        )
        res = self.client.get("/api/auth/users/?user_code=200000070")
        self.assertEqual(res.status_code, status.HTTP_200_OK)
        # 根据返回体结构校验
        data = res.json()
        results = data.get("data", {}).get("results", []) or data.get("data", [])
        self.assertEqual(len(results), 1)
        self.assertEqual(results[0]["real_name"], "副经理-石浩然")

    def test_prevent_delete_superuser_or_admin(self):
        # 尝试删除超级管理员账号
        res = self.client.delete(f"/api/auth/users/{self.admin_user.id}/")
        self.assertEqual(res.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn("严禁删除", str(res.data))
        # 验证数据库中用户依然存在
        self.assertTrue(User.objects.filter(id=self.admin_user.id).exists())

    def test_role_crud_and_unique_code(self):
        # 1. 创建自定义角色
        payload = {
            "code": "200200",
            "name": "市场",
            "sort_order": 1,
            "description": "负责客户对接与市场拓展"
        }
        res = self.client.post("/api/auth/roles/", payload, format="json")
        self.assertEqual(res.status_code, status.HTTP_201_CREATED)

        # 2. 校验重复编码阻断
        res_dup = self.client.post("/api/auth/roles/", payload, format="json")
        self.assertEqual(res_dup.status_code, status.HTTP_400_BAD_REQUEST)

        # 3. 获取列表
        res_list = self.client.get("/api/auth/roles/")
        self.assertEqual(res_list.status_code, status.HTTP_200_OK)

        # 4. 删除普通角色
        role_id = res.json()["data"]["id"]
        res_del = self.client.delete(f"/api/auth/roles/{role_id}/")
        self.assertEqual(res_del.status_code, status.HTTP_200_OK)
