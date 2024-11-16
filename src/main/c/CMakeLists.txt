cmake_minimum_required(VERSION 3.25.1)
project(beethoven C)

project(BeethovenLib VERSION 0.1.0 LANGUAGES C)
set(CMAKE_C_STANDARD 11)

set(FFMPEG_INCLUDE_DIR "/usr/include/x86_64-linux-gnu")
set(FFMPEG_LIB_DIR "/usr/lib/x86_64-linux-gnu")
find_library(AVFORMAT_LIB avformat PATHS ${FFMPEG_LIB_DIR})
find_library(AVCODEC_LIB avcodec PATHS ${FFMPEG_LIB_DIR})
find_library(AVUTIL_LIB avutil PATHS ${FFMPEG_LIB_DIR})

include_directories(include)

add_library(BeethovenLib SHARED
    audio/audio.c
    audio/audio.h
)

target_include_directories(BeethovenLib PRIVATE ${FFMPEG_INCLUDE_DIR})

target_link_libraries(BeethovenLib PRIVATE
    ${AVFORMAT_LIB}
    ${AVCODEC_LIB}
    ${AVUTIL_LIB}
)
