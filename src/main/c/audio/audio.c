#include <stdio.h>
#include <libavformat/avformat.h>

#include "audio.h"

double get_duration(const char *path) {
    AVFormatContext *formatContext = NULL;
    AVDictionary *opts = NULL;

    av_dict_set(&opts, "probesize", "32768", 0);
    av_dict_set(&opts, "analyzeduration", "5000000", 0);

    if (avformat_open_input(&formatContext, path, NULL, &opts) != 0) {
        fprintf(stderr, "Could not open: %s\n", path);
        return -1;
    }

    if (avformat_find_stream_info(formatContext, NULL) < 0) {
        fprintf(stderr, "Failed to find stream info\n");
        avformat_close_input(&formatContext);
        return -1;
    }

    double duration_seconds = 0;
    if (formatContext->duration > 0 && formatContext->duration != AV_NOPTS_VALUE) {
        duration_seconds = (double)formatContext->duration / AV_TIME_BASE;
    } else {
        for (unsigned int i = 0; i < formatContext->nb_streams; i++) {
            AVStream *stream = formatContext->streams[i];
            if (stream->duration > 0 && stream->time_base.den > 0) {
                double sec = stream->duration * av_q2d(stream->time_base);
                if (sec > duration_seconds)
                    duration_seconds = sec;
            } else if (stream->nb_frames > 0 && stream->time_base.den > 0) {
                double sec = stream->nb_frames * av_q2d(stream->time_base);
                if (sec > duration_seconds)
                    duration_seconds = sec;
            }
        }
    }

    avformat_close_input(&formatContext);
    av_dict_free(&opts);
    return duration_seconds;
}
