docker build -t grzybek/odysejapka .
ssh grzybek docker kill $(ssh grzybek docker ps -q --filter ancestor=grzybek/odysejapka )
docker save grzybek/odysejapka | bzip2 | pv | ssh grzybek docker load
ssh grzybek docker run -p 8081:8081 --net=host  -d grzybek/odysejapka