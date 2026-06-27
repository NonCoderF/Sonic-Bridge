class PCMPlayer extends AudioWorkletProcessor {

    constructor() {

        super();

        this.queue = [];

        this.current = null;

        this.offset = 0;

        // Keep only a few packets to minimize latency.
        this.MAX_QUEUE = 6;

        this.port.onmessage = (event) => {

            const pcm = new Int16Array(event.data);

            // Drop old packets if we fall behind.
            while (this.queue.length >= this.MAX_QUEUE) {
                this.queue.shift();
            }

            this.queue.push(pcm);

        };

    }

    process(inputs, outputs) {

        const output = outputs[0];

        if (output.length < 2)
            return true;

        const left = output[0];
        const right = output[1];

        left.fill(0);
        right.fill(0);

        let out = 0;

        while (out < left.length) {

            if (this.current == null) {

                if (this.queue.length == 0)
                    break;

                this.current = this.queue.shift();

                this.offset = 0;

            }

            while (

                this.offset + 1 < this.current.length &&

                out < left.length

            ) {

                left[out] =
                    this.current[this.offset++] / 32768.0;

                right[out] =
                    this.current[this.offset++] / 32768.0;

                out++;

            }

            if (this.offset >= this.current.length) {

                this.current = null;

            }

        }

        return true;

    }

}

registerProcessor(
    "pcm-player",
    PCMPlayer
);