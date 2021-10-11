#!/usr/bin/env python
import csv
import sys

PRICE_COL_IDX = 9
CHUNK_SIZE = 100


class Chunk:
    def __init__(self, key, count=0, sum_=0, sum2=0, size=0):
        self.key = key
        self.count = count
        self.sum = sum_
        self.sum2 = sum2
        self.size = size


def out_chunk(chunk, sep='\t'):
    mean = chunk.sum / chunk.size
    var = chunk.sum2 / chunk.size - (chunk.sum / chunk.size) ** 2
    print(chunk.key, chunk.size, mean, var, sep=sep)


chunk = None
for i, line in enumerate(csv.reader(sys.stdin)):
    if i == 0:
        continue
    if chunk is None:
        chunk = Chunk(i // CHUNK_SIZE)

    price = float(line[PRICE_COL_IDX])
    chunk.size += 1
    chunk.sum += price
    chunk.sum2 += price ** 2

    if i % CHUNK_SIZE == 0:
        out_chunk(chunk)
        chunk = None

if chunk is not None:
    out_chunk(chunk)
