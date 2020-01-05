import os

dot_path = '/mnt/share/test/full/'
img_path = '/mnt/share/test/full_image/'

for p in os.listdir(dot_path):
    print(p)
    with open(dot_path + p) as f:
        name = f.name
        print(name)
        tmp = name.split("/")[-1]
        id = tmp[0:tmp.index('.')]
        os.system("dot " + str(name) + " -Tpng -o" + img_path + str(id) + ".png")
