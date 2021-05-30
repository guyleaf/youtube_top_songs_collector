#
#  Author: GuyLeaf
#  Date: 2021-06-29 23:35:57 GMT+08
#  https://github.com/guyleaf/youtube_top_songs_collector
#

FROM guyleaf/ubuntu-java-bionic:latest
MAINTAINER GuyLeaf (https://github.com/guyleaf)

ARG HBASE_VERSION=2.4.2

ARG TARGET=hbase-${HBASE_VERSION}-bin.tar.gz

ENV PATH $PATH:/hbase/bin

LABEL Description="HBase standalone"

WORKDIR /

RUN set -eux && apt-get update

RUN apt-get upgrade -y

RUN set -eux && \
    apt-get install -y openssh-server openssh-client tar

RUN set -eux && wget --retry-connrefused "https://downloads.apache.org/hbase/${HBASE_VERSION}/$TARGET" && \
    mkdir "hbase" && tar zxf "$TARGET" -C "hbase" --strip 1 && test -d "hbase"

COPY profile.d/hbase.sh /etc/profile.d
COPY entrypoint.sh /

RUN chmod +x /entrypoint.sh

# HMaster   16000 / 16010
EXPOSE 16000 16010

CMD ["/entrypoint.sh"]