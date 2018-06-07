from .common import *
import json
DEBUG = False


path = os.path.join(BASE_DIR, 'envs.json')

with open(path) as f:
    dict = json.load(f)
    SECRET_KEY = dict["SECRET_KEY"]
    ALLOWED_HOSTS = [dict["PUBLIC_IP"], '127.0.0.1', 'localhost']
