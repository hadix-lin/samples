# 使用Docker运行
使用`docker run`运行服务
```bash
# 创建一个自定义bridge network，后续所有的容器都连接到这个网络上以便相互访问
docker network create counter-net

# 运行mysql 5，该命令会自动从Docker Hub拉取镜像
docker run --rm \
--network counter-net \
--name mysql \ # 容器名称会成为网络中的主机名
-e MYSQL_ROOT_PASSWORD=12345678 \ # 设置一个环境变量
-d mysql:5 # 

# 运行redis，该命令会自动从Docker Hub拉去镜像redis
docker run --rm \
--name redis \ # 容器名称会成为网络中的主机名
--network counter-net\
-d redis

# 从samples仓库中签出simple-counter
git init simple-counter
cd simple-counter
git remote add origin https://github.com/hadix-lin/samples.git
git config core.sparsecheckout true
echo "simple-counter/" >> .git/info/sparse-checkout
git pull origin master
# 构建simple-counter
mvn package
cp target/simple-counter-1.0-SNAPSHOT.jar docker-image/
cd docker-image
# 在docker-image文件夹中有Dockerfile文件
docker build -t simple-counter .
docker run -p 8080:8080 \ #开放在主机上的访问
--rm \ # 容器关闭后自动删除
--name simple-counter \ # 容器名称
--network counter-net \ # 加入的网络
-d simple-counter
```
或者使用`docker-compose`运行服务
```bash
# 从samples仓库中签出simple-counter
git init simple-counter
cd simple-counter
git remote add origin git@github.com:hadix-lin/samples.git
git config core.sparsecheckout true
echo "simple-counter/" >> .git/info/sparse-checkout
git pull origin master
# 构建simple-counter
mvn package
cp target/simple-counter-1.0-SNAPSHOT.jar docker-image/
cd docker-image
docker-compose up
```
