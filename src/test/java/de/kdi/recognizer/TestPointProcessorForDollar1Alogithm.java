package de.kdi.recognizer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.kdi.pojo.Point;
import de.kdi.recognizer.$1.ResampleFirstPointProcessor;
import de.kdi.recognizer.$1.ResampleLastPointProcessor;

public class TestPointProcessorForDollar1Alogithm {

    private static final double SQUARE_SIZE = 250.0;
	private static final int N = 64;
	
	/**
	 * Name of gesture: x
	 * 0.8523159
	 * 0.93974507
	 * 
	 * */
	private static final String POINT_LIST_FIXTURE = "[{\"x\":-122.67611931388473,\"y\":5.6843418860808015E-14},{\"x\":-110.31279552117218,\"y\":-3.504024200061508},{\"x\":-96.0355882030666,\"y\":-5.053703644476002},{\"x\":-81.83828504125216,\"y\":-6.997520670074664},{\"x\":-67.59777719942724,\"y\":-8.791983641207594},{\"x\":-53.355404618300895,\"y\":-10.580000403847748},{\"x\":-39.11303203717449,\"y\":-12.368017166487903},{\"x\":-24.870659456048145,\"y\":-14.156033929128},{\"x\":-10.628286874921741,\"y\":-15.944050691768098},{\"x\":3.601638076466429,\"y\":-17.767540204619166},{\"x\":17.525614144052042,\"y\":-20.46291043377076},{\"x\":31.44959021163777,\"y\":-23.15828066292235},{\"x\":45.373566279223496,\"y\":-25.853650892073915},{\"x\":59.29754234680911,\"y\":-28.549021121225508},{\"x\":73.22382309685156,\"y\":-31.238573505140977},{\"x\":87.29078433854318,\"y\":-33.57299786448331},{\"x\":101.35774558023479,\"y\":-35.90742222382573},{\"x\":115.54483853357362,\"y\":-37.877283535478284},{\"x\":127.32388068611522,\"y\":-37.183670870043045},{\"x\":121.26742225144699,\"y\":-28.334041614190824},{\"x\":114.31546683404997,\"y\":-19.77326015435372},{\"x\":107.36351141665273,\"y\":-11.21247869451662},{\"x\":99.23389427263157,\"y\":-3.1429415735949533},{\"x\":90.95144114017603,\"y\":4.862842253487258},{\"x\":82.6689880077206,\"y\":12.868626080569584},{\"x\":74.38653487526506,\"y\":20.874409907651795},{\"x\":66.10408174280963,\"y\":28.880193734734064},{\"x\":56.875198766599624,\"y\":36.397505369042165},{\"x\":47.551240617945496,\"y\":43.86574672279153},{\"x\":38.227282469291254,\"y\":51.33398807654089},{\"x\":28.903324320637125,\"y\":58.802229430290254},{\"x\":19.579366171982883,\"y\":66.27047078403956},{\"x\":10.255408023328641,\"y\":73.73871213778904},{\"x\":0.9205622983830608,\"y\":81.20069067355149},{\"x\":-8.572866158230966,\"y\":88.57144828394757},{\"x\":-18.066294614844992,\"y\":95.94220589434366},{\"x\":-26.897931757605363,\"y\":103.66552043484114},{\"x\":-35.8555656155433,\"y\":111.32171258467565},{\"x\":-35.83845061386194,\"y\":104.68222267987528},{\"x\":-36.26154833906287,\"y\":94.92882999420766},{\"x\":-36.68464606426386,\"y\":85.17543730854015},{\"x\":-37.10774378946479,\"y\":75.42204462287259},{\"x\":-37.330805812587016,\"y\":65.66623155914755},{\"x\":-37.42814161680843,\"y\":55.9088972420746},{\"x\":-37.525477421029734,\"y\":46.15156292500171},{\"x\":-37.62281322525109,\"y\":36.394228607928824},{\"x\":-37.720149029472395,\"y\":26.636894290855935},{\"x\":-37.81748483369381,\"y\":16.87955997378299},{\"x\":-37.91482063791511,\"y\":7.122225656710043},{\"x\":-38.01215644213653,\"y\":-2.635108660362903},{\"x\":-37.960129615707444,\"y\":-12.392043805533717},{\"x\":-37.74782583217291,\"y\":-22.148550610245337},{\"x\":-37.53552204863843,\"y\":-31.9050574149569},{\"x\":-37.32321826510395,\"y\":-41.66156421966849},{\"x\":-37.11091448156952,\"y\":-51.41807102438008},{\"x\":-36.89861069803504,\"y\":-61.174577829091675},{\"x\":-36.686306914500506,\"y\":-70.9310846338033},{\"x\":-36.47400313096608,\"y\":-80.68759143851483},{\"x\":-35.09090028196351,\"y\":-90.38217254982294},{\"x\":-33.039298254426,\"y\":-100.04139552292321},{\"x\":-30.987696226888488,\"y\":-109.70061849602351},{\"x\":-28.936094199350975,\"y\":-119.35984146912381},{\"x\":-26.884492171813463,\"y\":-129.01906444222408},{\"x\":-24.83289014427595,\"y\":-138.67828741532435}]";
	
	private List<Point> points;

	@Before
	public void setup(){
		points = new ArrayList<Point>();
		Gson value = new Gson();
		points = value.fromJson(POINT_LIST_FIXTURE, new TypeToken<List<Point>>(){}.getType());
		
	}

	@Test	
	public void testTransformationFirstResample(){
		List<Point> firstResamplePointList = $1transformationsFirstResample();
		ResampleFirstPointProcessor proc = new ResampleFirstPointProcessor(points);
		assertEquals(firstResamplePointList, proc.preparePointsFor$1());
	}
	
	@Test	
	public void testTransformationLastResample(){
		List<Point> lastResamplePointList = $1transformationsLastResample();
		ResampleLastPointProcessor proc = new ResampleLastPointProcessor(points);
		assertEquals(lastResamplePointList, proc.preparePointsFor$1());
	}
	
	private List<Point> $1transformationsFirstResample() {
    	List<Point> ptsResampledFirst = cloneArray(points);
    	ptsResampledFirst = Point.resample(ptsResampledFirst, N);
    	ptsResampledFirst = processPoints(ptsResampledFirst);
    	return ptsResampledFirst;
    }

	private List<Point> $1transformationsLastResample() {
		List<Point> ptsResampledLast = cloneArray(points);
    	ptsResampledLast = processPoints(ptsResampledLast); 
    	ptsResampledLast = Point.resample(ptsResampledLast, N);
		return ptsResampledLast;
	}
    
    private List<Point> cloneArray(List<Point> points){
		List<Point> processedPoints = new ArrayList<Point>(Arrays.asList(new Point[points.size()]));
		Collections.copy(processedPoints, points);
		return processedPoints;
	}

	private List<Point> processPoints(List<Point> points) {
		//Rotate to 0 degree
		points = Point.rotate(points);
		//Scale to square
		points = Point.scale(points, SQUARE_SIZE);
		//Translate to origin
		points = Point.translate(points);
		return points;
	}
	
}
