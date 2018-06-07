from .common import *
import json

path = os.path.join(BASE_DIR, 'envs.json')

with open(path) as f:
    dict = json.load(f)
    SECRET_KEY = dict["SECRET_KEY"]
