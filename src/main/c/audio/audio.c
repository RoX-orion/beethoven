#include <stdio.h>
#include <libavformat/avformat.h>

#include "audio.h"

int get_duration(char *path) {

    AVFormatContext *formatContext = NULL;

    if (avformat_open_input(&formatContext, path, NULL, NULL) != 0) {
        printf("Could not open the file: %s\n", path);
        return -1;
    }

    if (avformat_find_stream_info(formatContext, NULL) < 0) {
        printf("Could not retrieve stream information from the file: %s\n", path);
        avformat_close_input(&formatContext);
        return -1;
    }

    int64_t duration = formatContext -> duration;
    int duration_seconds = (int)duration / AV_TIME_BASE;
    avformat_close_input(&formatContext);

    return duration_seconds;
}
