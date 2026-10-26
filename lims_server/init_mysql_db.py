import os
import pymysql
from dotenv import load_dotenv

load_dotenv()

host = os.environ.get('DB_HOST', '127.0.0.1')
port = int(os.environ.get('DB_PORT', 3306))
user = os.environ.get('DB_USER', 'root')
password = os.environ.get('DB_PASSWORD', '')
dbname = os.environ.get('DB_NAME', 'lims_db')

try:
    conn = pymysql.connect(host=host, port=port, user=user, password=password)
    with conn.cursor() as cursor:
        cursor.execute(f"CREATE DATABASE IF NOT EXISTS `{dbname}` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;")
        cursor.execute("SHOW DATABASES LIKE %s;", (dbname,))
        res = cursor.fetchone()
        print(f"DATABASE_STATUS: database '{res[0]}' is ready.")
    conn.close()
except Exception as e:
    print(f"DATABASE_ERROR: {e}")
