#!/usr/bin/env python
import sys

PRICE_COL_IDX = 9
CHUNK_SIZE = 1000

acc_count, acc_mean, acc_var = 0, 0, 0
for line in sys.stdin:
    _, c, m, v = line.strip().split()
    c, m, v = int(c), float(m), float(v)
    new_acc_count = (acc_count + c)
    acc_var = (acc_count * acc_var + c * v) / new_acc_count + acc_count * c * ((acc_mean - m) / new_acc_count) ** 2
    acc_mean = (acc_count * acc_mean + c * m) / new_acc_count
    acc_count = new_acc_count
print(acc_mean, acc_var, sep='\t')
