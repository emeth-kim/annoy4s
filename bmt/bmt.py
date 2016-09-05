from annoy import AnnoyIndex
import random
import time

dim = 50
num_items = 1000000

s = time.time()
items = []
for i in range(num_items):
    vec = []
    for j in range(dim):
        vec.append(random.random())
    items.append((i, vec))
print('build items %s' % (time.time() - s))

a = AnnoyIndex(dim)

s = time.time()
for i, vec in items:
    a.add_item(i, vec)

a.build(10)
print('build tree %s' % (time.time() - s))

s = time.time()
for i in range(num_items):
    a.get_nns_by_item(i, 10)
print('query %s' % (time.time() - s))
