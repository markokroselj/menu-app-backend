FROM python:alpine3.24

WORKDIR /usr/src/app

RUN pip install --no-cache-dir pdfminer

CMD [ "pdf2txt.py", "-o", "/data/input.txt", "-t", "text",  "/data/download.pdf"  ]