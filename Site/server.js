const express = require('express');
const path = require('path');
const app = express();

// Serve static files from the Vue build output folder
app.use(express.static(path.join(__dirname, 'dist')));

// Send index.html for any other request
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist/index.html'));
});

// Start the server
const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Server started on port ${port}`);
});

