const multer = require('multer');
const path = require('path');

// Configure multer
const MIME_TYPE_MAP = {
  'image/png': 'png',
  'image/jpeg': 'jpg',
  'imagejpg': 'jpg'
}



module.exports = (subFolder) => multer({storage:
    multer.diskStorage({
        destination: (req, file, cback)=> {
          const isValid = MIME_TYPE_MAP[file.mimetype];
          let error = new Error('Invalid Mime Type');
      
          if(isValid) {
            error = null;
          }
          cback(error, path.join(__dirname, '../images/' + subFolder));
        },
        filename: (req, file, cback)=> {
          const name = file.fieldname.toLowerCase().split(' ').join('-');
          const ext = MIME_TYPE_MAP[file.mimetype];
      
          cback(null, name + '-' + Date.now() + '.' + ext);
        }
      
      })
    }).single('image');