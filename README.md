# ReadMyPhoto

Android application to copy text from your photos into your clipboard


## Download

[Google Play Store](https://play.google.com/store/apps/details?id=io.msol.readmyphoto)


### Known issues

Sometimes ReadMyPhoto does a poor job reading the text out of a photo, especially if it is not a picture of a block of text. This could be fixed by adding cropping and rotation before the image is "read" by Tesseract.

Feel free to jump in and do that if you have the interest!

### Libraries

ReadMyPhoto uses [tess-two](https://github.com/rmtheis/tess-two) to interact with the [Tesseract OCR library](https://code.google.com/p/tesseract-ocr/) to read the text out of photos.
