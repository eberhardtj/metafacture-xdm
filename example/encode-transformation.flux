"example.xml"
| open-file
| decode-xml
| sax-to-xdm
| encode-transformation("example.xsl")
| print;