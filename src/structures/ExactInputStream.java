package structures;

import java.io.IOException;
import java.io.InputStream;

/**
 * ExactInputStream.<br><br>
 * 
 * Created: 29.05.2008<br>
 * Last modified: 29.05.2008
 * 
 * @author Thomas Stroder
 * @version 1.0
 */
public class ExactInputStream extends InputStream {

    private InputStream stream;
    
    public ExactInputStream(InputStream stream) {
        if (stream == null) {
            throw new NullPointerException("Stream must not be null!");
        }
        this.stream = stream;
    }
    
    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        return this.stream.read();
    }

    public void read(byte[] buffer, int read) throws IOException {
        if (buffer.length < read) {
            throw new IllegalArgumentException("Cannot read more bytes than the length of the buffer!");
        }
        int pointer = 0;
        while (read > 0) {
            byte[] toRead = new byte[read];
            int done = this.stream.read(toRead);
            if (done == -1) {
                throw new IOException("End of stream reached before reading all bytes!");
            }
            System.arraycopy(toRead,0,buffer,pointer,done);
            pointer += done;
            read -= done;
        }
    }

}
