degreeSequence生成：
java -jar degreeSequence.jar 数目 路径 degreeDis的文件名全名 输出文件名（全名）

java -jar degreeSequence.jar 100000 /Users/ycc/kuaipan/workspace/SocialStreamGen/data/GenProducerNetwork/patent/ Deg.txt degree_100000.txt

CLGRaphGen.py:

python CLGRaphGen.py degreeSequence文件路径（包括文件名） 输出文件路径

python CLGraphGen.py /Users/ycc/kuaipan/workspace/SocialStreamGen/data/GenProducerNetwork/patent/degree_100000.txt /Users/ycc/kuaipan/workspace/SocialStreamGen/data/GenProducerNetwork/patent/patent_100000.txt