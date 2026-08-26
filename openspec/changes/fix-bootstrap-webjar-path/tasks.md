## 1. Correct Dashboard Asset References

- [x] 1.1 Update the Dashboard Bootstrap stylesheet reference to `/webjars/bootstrap/5.3.8/dist/css/bootstrap.min.css`.
- [x] 1.2 Update the Dashboard Bootstrap bundle reference to `/webjars/bootstrap/5.3.8/dist/js/bootstrap.bundle.min.js`.

## 2. Verify Offline Resource Resolution

- [x] 2.1 Add or update a focused verification that the Dashboard references match files present in the Bootstrap 5.3.8 WebJar and contain no external Bootstrap URL.
- [x] 2.2 Run the project test and build checks, including the focused resource-path verification.
- [x] 2.3 With the application running, request both Dashboard Bootstrap resources and confirm successful same-origin responses.