const { exec } = require('child_process');
const fs = require('fs');
const os = require('os');
const path = require('path');

function read(file_name) {
  return new Promise((resolve, reject) => {
    // Generate a temporary file path
    const temp_file = path.join(os.tmpdir(), `temp_${Date.now()}.json`);
    const moduleDir = path.dirname(__filename);
    const classpath = path.resolve(moduleDir, 'lib');

    // Command to execute
    const command = `java -cp "${classpath}" net.sf.mpxj.ruby.GenerateJson "${file_name}" "${temp_file}"`;

    // Execute the command
    exec(command, (error, stdout, stderr) => {
      if (error) {
        reject(`Error executing command: ${error.message}`);
        return;
      }
      if (stderr) {
        reject(`Command error: ${stderr}`);
        return;
      }

      // Read the contents of the temporary file
      fs.readFile(temp_file, 'utf8', (err, data) => {
        if (err) {
          reject(`Error reading file: ${err.message}`);
          return;
        }

        // Parse JSON data to an object
        let jsonData;
        try {
          jsonData = JSON.parse(data);
        } catch (parseErr) {
          reject(`Error parsing JSON: ${parseErr.message}`);
          return;
        }

        // Resolve with the parsed JSON object
        resolve(jsonData);

        // Cleanup: Remove the temporary file
        fs.unlink(temp_file, (unlinkErr) => {
          if (unlinkErr) {
            console.error(`Error deleting temporary file: ${unlinkErr}`);
          }
        });
      });
    });
  });
}

module.exports = { read };
