import csv
import random

num_astroids = 1000;
rock = range(4)
size = range(1,9)
posx = range(-1000,1001)
posy = range(-1000,1001)
posz = range(-1000,1501)
angle = range(91)

with open('AstroidPositions.csv','w',newline='') as f:
    writer = csv.writer(f, delimiter=';')
    for line in range(num_astroids):
        data = []
        data.append(random.choice(rock))
        data.append(random.choice(posx))
        data.append(random.choice(posy))
        data.append(random.choice(posz))
        data.append(random.choice(size))
        data.append(random.choice(angle))
        data.append(random.choice(angle))
        data.append(random.choice(angle))
        writer.writerow(data)
    
    