"example.xml"
| open-file
| decode-xml
| sax-to-xdm
| transform("example.xsl")
| xdm-to-xml
| print;