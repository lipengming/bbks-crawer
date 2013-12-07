package bimoku.crawler.queue;
import java.util.ArrayList;

public class IpProxylist {

	
	private static ArrayList<IpProxy> queue = new ArrayList<IpProxy>();
	
	public static void addAproxy(IpProxy t) {
	queue.add(t);

	}
	
	public static IpProxy getAproxy(int index) {
	return queue.get(index);
	}
	
	public static boolean isQueueEmpty() {
	return queue.isEmpty();
	}
	
	public static boolean contians(IpProxy t) {
	return queue.contains(t);
	}
	public  static boolean empty() {
	return queue.isEmpty();
	}
	public  static int size(){
		return queue.size();
	}
}
