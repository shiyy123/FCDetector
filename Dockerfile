FROM buildpack-deps:xenial-curl

MAINTAINER CScanner

RUN echo "deb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial main restricted universe multiverse\ndeb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-security main restricted universe multiverse\ndeb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-updates main restricted universe multiverse\ndeb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-backports main restricted universe multiverse\ndeb http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-proposed main restricted universe multiverse\ndeb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial main restricted universe multiverse\ndeb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-security main restricted universe multiverse\ndeb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-updates main restricted universe multiverse\ndeb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-backports main restricted universe multiverse\ndeb-src http://mirrors.tuna.tsinghua.edu.cn/ubuntu/ xenial-proposed main restricted universe multiverse" >/etc/apt/sources.list

RUN apt-get update && \
apt-get install -y --no-install-recommends apt-utils \
python3-pip
openjdk-8-jdk-headless && \
rm -rf /var/lib/apt/lists/*

RUN echo "[global] index-url = https://pypi.tuna.tsinghua.edu.cn/simple [install] trusted-host=mirrors.aliyun.com" >~/.pip/pip.conf
RUN pip3 install torch==1.3.1+cpu torchvision==0.4.2+cpu -f https://download.pytorch.org/whl/torch_stable.html

COPY . /scanner

ENV PATH /scanner/Bash:${PATH}
ENV LANG C.UTF-8

WORKDIR /workspace
ENTRYPOINT ["/bin/sh", "/scanner/bin/CloneDetect"]
