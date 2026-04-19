n = int(input())
for i in range(n):
    a = int(input())
    b = []
    for j in range(a):
        x = int(input())
        b.append(x)
    for k in range(a):
        if b[k] % 2 == 0:
            print(b[k] + " ")
