# Use latest node version
FROM bigtruedata/sbt:latest

MAINTAINER Benoît Schopfer <benoit.schopfer@heig-vd.ch.ch>

# create backend directory in container
RUN mkdir -p /backend

# set /backend directory as default working directory
WORKDIR /backend

# only copy build.sbt initially so that it's recreated only if there are changes in build.sbt
ADD build.sbt ./
COPY ./project ./project


RUN sbt compile

# copy all file from current dir to /backend in container
COPY . ./


# expose port 9000 and 3306
EXPOSE 9000 9443 3306

# cmd to start service
CMD [ "sbt", "run" ]
