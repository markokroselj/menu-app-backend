# Menu app backend

Automated, Docker Compose based pipeline that converts a restaurant's monthly menu PDF into JSON, publishes it to an HTTP server, and triggers a [frontend](http://github.com/markokroselj/menu-app) rebuild. 

## How it works
 
```
Menu PDF ──► pdfminer ──► .txt ──► Java parser ──► .json ──► SFTP upload ──► Vercel webhook
```
 
1. **PDF to text.** The monthly menu PDF is converted to a plain text file with [pdfminer](https://pypi.org/project/pdfminer/).
2. **Text to JSON.** A [Java](./textToJson/) program parses the text file and produces the structured menu JSON.
3. **Upload.** The JSON file is uploaded to the HTTP server over SFTP.
4. **Rebuild.** A `curl` call to a webhook triggers a new Vercel build of the <abbr title="Server Site Generation">SSG</abbr> frontend, so the site picks up the new menu.

## Usage 

1. Download Docker compose file
``curl -O https://raw.githubusercontent.com/markokroselj/menu-app-backend/main/compose.yml``.
2. Download .env template.1
``curl -o .env https://raw.githubusercontent.com/markokroselj/menu-app-backend/main/deploy/.env.example``.
3. Replace secrets in `.env` with yours.
4. Make `date` directory. `mkdir data`.
5. Copy menu PDF to data dir and name it `download.pdf`.
6. Run run pipeline with ``docker compose up``.