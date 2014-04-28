import java.io.IOException;

import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.Pail.TypedRecordOutputStream;

public class SimpleIO {

	public SimpleIO() {
		// TODO Auto-generated constructor stub
	}

	public static void simpleIO() throws IOException {
		Pail pail = Pail.create("/Users/eniesc200/Tmp/pails/mypail");
		TypedRecordOutputStream os = pail.openWrite();

		os.writeObject(new byte[] { 1, 2, 3 });
		os.writeObject(new byte[] { 1, 2, 3, 4 });
		os.writeObject(new byte[] { 1, 2, 3, 4, 5 });

		os.close();
	}

	public static void main(String[] args) {
		try {
			SimpleIO.simpleIO();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
