"example.xml"
| open-file
| decode-xml
| sax-to-xdm
| transform(".xsl")
| xdm-to-xml
| print;