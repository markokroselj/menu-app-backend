FROM alpine:latest

RUN apk add --no-cache openssh-client sshpass curl

COPY deploy.sh /usr/local/bin/upload.sh
RUN chmod +x /usr/local/bin/upload.sh

ENTRYPOINT ["/usr/local/bin/upload.sh"]