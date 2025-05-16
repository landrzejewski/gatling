import socket

s = socket.socket()
s.bind(("localhost", 2003))
s.listen(1)
print("Listening...")
conn, addr = s.accept()
print(f"Connected by {addr}")
while True:
    data = conn.recv(1024)
    if not data:
        break
    print(data.decode())
