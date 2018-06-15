from .common import *
import json
DEBUG = False


BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname((os.path.abspath(__file__)))))
path = os.path.join(BASE_DIR, 'envs.json')
STATIC_ROOT = os.path.join(BASE_DIR, "static/")
with open(path) as f:
    dict = json.load(f)
    SECRET_KEY = dict["SECRET_KEY"]
    ALLOWED_HOSTS = [dict["PUBLIC_IP"], '127.0.0.1', 'localhost']

