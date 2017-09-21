package xyz.upperlevel.openverse.client.render.world;

import lombok.RequiredArgsConstructor;
import xyz.upperlevel.openverse.Openverse;
import xyz.upperlevel.openverse.client.render.world.util.VertexBuffer;
import xyz.upperlevel.openverse.client.render.world.util.VertexBufferPool;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

@RequiredArgsConstructor
public class ChunkCompileTask {
    private final VertexBufferPool bufferPool;
    private final ChunkRenderer chunk;
    private ReentrantLock stateLock = new ReentrantLock();
    private VertexBuffer buffer;
    private int vertexCount;
    private State state = State.PENDING;

    protected void askBufer() {
        if (buffer == null) {
            try {
                buffer = bufferPool.waitForBuffer();
            } catch (InterruptedException e) {
                Openverse.logger().log(Level.WARNING, "Chunk compiler: interrupted pool retrieving");
            }
        }
    }

    public void compile() {
        stateLock.lock();
        try {
            if (state == State.ABORTED) {
                return;
            }
            if (state != State.PENDING) {
                throw new IllegalArgumentException("Cannot compile an already compiled chunk (state: " + state + ")");
            }
            state = State.COMPILING;
        } finally {
            stateLock.unlock();
        }

        askBufer();
        if (buffer == null) {
            //Buffer retrieving failed
            return;
        }

        buffer.ensureCapacity(chunk.getAllocateDataCount() * Float.BYTES);
        try {
            vertexCount = chunk.compile(buffer.byteBuffer());
        } catch (Exception e) {
            Openverse.logger().log(Level.SEVERE, "Error while compiling chunk (data:" + (chunk.getAllocateDataCount() * Float.BYTES) + ", cap:" + buffer.byteBuffer().capacity() + ")", e);
            bufferPool.release(buffer);
            return;
        }
        stateLock.lock();
        try {
            if (state == State.ABORTED) {
                return;
            }
            state = State.UPLOADING;
        } finally {
            stateLock.unlock();
        }
    }

    public void useBuffer(VertexBuffer buffer) {
        stateLock.lock();
        try {
            if (state != State.PENDING) {
                return;
            }
            this.buffer = buffer;
        } finally {
            stateLock.unlock();
        }
    }

    public void upload() {
        try {
            if (isValid()) {
                chunk.setVertices(buffer.byteBuffer(), vertexCount);
                stateLock.lock();
                try {
                    state = State.DONE;
                } finally {
                    stateLock.unlock();
                }
            }
        } finally {
            buffer.release();
        }
    }

    public void completeNow() {
        State state;
        stateLock.lock();
        try {
            state = this.state;
        } finally {
            stateLock.unlock();
        }
        switch (state) {
            case PENDING:
                compile();
                //Note the missing "break;" is not a mistake, it compiles and then uploads
            case UPLOADING:
                upload();
            default:
                break;
        }
    }

    public boolean isValid() {
        stateLock.lock();
        try {
            return state != State.ABORTED;
        } finally {
            stateLock.unlock();
        }
    }

    public boolean abort() {
        stateLock.lock();
        try {
            if (state != State.ABORTED) {
                state = State.ABORTED;
                return true;
            }
            return false;
        } finally {
            stateLock.unlock();
        }
    }


    public enum State {
        PENDING,
        COMPILING,
        UPLOADING,
        DONE,
        ABORTED
    }
}