package parser;

import java.util.ArrayList;

public class UppaalTemplate {
	private ArrayList<UppaalBranchpoint> branchpoints;
	private ArrayList<UppaalTransition> transitions;
	private ArrayList<UppaalLocation> locations;
	private ArrayList<UppaalVariable> variables;
	private ArrayList<String> declarations;

	private String nameLocationX;
	private String nameLocationY;
	private String name;
	private String init;

	private int computationTime;
	private int templateId;
	private int priority;
	private int deadline;
	private int period;
	private int id;
	private int x;
	private int y;

	protected static final int GUARD = 1;
	protected static final int SYNC = 2;
	protected static final int ASSIG = 3;
	protected static final int INVA = 4;
	protected static final int EXPO = 5;
	protected static final int COMM = 6;
	protected static final int PROB = 7;	
	
	public UppaalTemplate() {
		declarations = new ArrayList<>();

		transitions = new ArrayList<>();

		variables = new ArrayList<>();

		//functions = new ArrayList<>();

		locations = new ArrayList<>();

		branchpoints = new ArrayList<>();

		name = "";
		nameLocationX = "";
		nameLocationY = "";

		templateId = 0;
		priority = 0;

		id = 0;
		x = 0;
		y = 0;
	}

	public void setLocationInvariant(String name, String invariant){
		for(UppaalLocation l : locations){
			if(l.getName().equalsIgnoreCase(name)){
				l.addLabel(new UppaalLabel(invariant, "invariant"));
			}
		}
	}
	
	public void setLocationExponentialRate(String name, String invariant){
		for(UppaalLocation l : locations){
			if(l.getName().equalsIgnoreCase(name)){
				l.addLabel(new UppaalLabel(invariant, "exponentialrate"));
				//System.out.println("exponentialrate added " + invariant + " " + l.getName());
			}
		}
	}
	
	public String searchLocationBranchpoint(String id){
		for(UppaalBranchpoint b : branchpoints){
			if(b.getLocationId().equalsIgnoreCase(id))
				return b.getId();
		}
		return "";
	}

	public void setVariableInitValue(String name, String value){
		//System.out.println("init value " + name + " " + value);
		for(UppaalVariable v : variables){
			if(v.getName().equalsIgnoreCase(name)){
				v.setInitialValue(value);
				//System.out.println("added");
			}
		}
	}
	
	public void addBranchpoint(UppaalBranchpoint b){
		branchpoints.add(b);
	}

	public void addVariable(UppaalVariable v){
		variables.add(v);
	}

	public ArrayList<UppaalVariable> getVariables(){
		return this.variables;
	}

	public int getComputationTime() {
		return computationTime;
	}

	public void setComputationTime(int computationTime) {
		this.computationTime = computationTime;
	}

	public int getDeadline() {
		return deadline;
	}

	public void setDeadline(int deadline) {
		this.deadline = deadline;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return this.id;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void incrementId() {
		id++;
	}

	public void incrementX() {
		x += 70;
	}

	public void incrementY() {
		y += +70;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getDeclarations() {
		return declarations;
	}

	public void setDeclarations(ArrayList<String> declarations) {
		this.declarations = declarations;
	}

	public void addDeclaration(String dec) {
		this.declarations.add(dec);
	}

	public ArrayList<UppaalLocation> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<UppaalLocation> locations) {
		this.locations = locations;
	}

	public String getInit() {
		return init;
	}

	public void setInit(String init) {
		this.init = init;
	}

	public String getNameLocationX() {
		return nameLocationX;
	}

	public void setNameLocationX(String nameLocationX) {
		this.nameLocationX = nameLocationX;
	}

	public String getNameLocationY() {
		return nameLocationY;
	}

	public void setNameLocationY(String nameLocationY) {
		this.nameLocationY = nameLocationY;
	}

	public ArrayList<UppaalTransition> getTransitions() {
		return transitions;
	}

	public void setTransitions(ArrayList<UppaalTransition> transitions) {
		this.transitions = transitions;
	}

	public void addLocation(UppaalLocation l) {
		this.locations.add(l);
	}

	public void addTransition(UppaalTransition t) {
		this.transitions.add(t);
	}

	public void createScheduler() {
		UppaalNail n;
		UppaalLocation l;
		UppaalTransition t;
		UppaalLabel label;

		this.name = "scheduler";
		this.nameLocationX = "5";
		this.nameLocationY = "5";

		String d = "// Place local declarations here.\n";
		declarations.add(d);

		l = new UppaalLocation("id0", "-272", "-17", "NewRequest", "-263", "-25", false);

		locations.add(l);

		l = new UppaalLocation("id1", "-170", "34", "Run", "-161", "8", false);

		locations.add(l);

		l = new UppaalLocation("id2", "-170", "-68", "Select", "-161", "-93", true);

		locations.add(l);

		l = new UppaalLocation("id3", "-170", "-170", "NewTasks", "-161", "-195", false);

		locations.add(l);

		l = new UppaalLocation("id4", "-170", "-272", "Free", "-212", "-297", true);

		locations.add(l);

		l = new UppaalLocation("id5", "-170", "-374", "Init", "-180", "-408", true);

		locations.add(l);

		this.init = "id5";

		// t1
		t = new UppaalTransition("id1", "id4");
		label = new UppaalLabel("done?", 2, "-119", "-127");
		t.addLabel(label);
		n = new UppaalNail("-68", "34");
		t.addNail(n);
		n = new UppaalNail("-68", "-272");
		t.addNail(n);
		transitions.add(t);

		// t2
		t = new UppaalTransition("id4", "id2");
		label = new UppaalLabel("!isEmpty() && mLoaded == 1", 1, "-263", "-161");
		t.addLabel(label);
		n = new UppaalNail("-272", "-272");
		t.addNail(n);
		n = new UppaalNail("-272", "-68");
		t.addNail(n);
		transitions.add(t);

		// t3
		t = new UppaalTransition("id0", "id2");
		label = new UppaalLabel("stop!", 2, "-263", "-59");
		t.addLabel(label);
		n = new UppaalNail("-272", "-68");
		t.addNail(n);
		transitions.add(t);

		// t4
		t = new UppaalTransition("id1", "id0");
		label = new UppaalLabel("ready?", 2, "-263", "17");
		t.addLabel(label);
		n = new UppaalNail("-272", "34");
		t.addNail(n);
		transitions.add(t);

		// t5
		t = new UppaalTransition("id2", "id1");
		label = new UppaalLabel("mStarted()", 1, "-161", "-51");
		t.addLabel(label);
		label = new UppaalLabel("run!", 2, "-161", "-34");
		t.addLabel(label);
		transitions.add(t);

		// t6
		t = new UppaalTransition("id3", "id2");
		label = new UppaalLabel("ready?", 2, "-229", "-119");
		t.addLabel(label);
		transitions.add(t);

		// t7
		t = new UppaalTransition("id4", "id3");
		label = new UppaalLabel("isEmpty()", 1, "-246", "-238");
		t.addLabel(label);
		transitions.add(t);

		// t8
		t = new UppaalTransition("id5", "id4");

		label = new UppaalLabel("initializeSched()", 3, "-161", "-331");
		t.addLabel(label);
		label = new UppaalLabel("systemInitialized()", 1, "-161", "-348");
		t.addLabel(label);
		transitions.add(t);		
	}

	public void createBehavior() {
		UppaalNail n;
		UppaalLocation l;
		UppaalTransition t;
		UppaalLabel label;

		this.name = "behavior";
		this.nameLocationX = "5";
		this.nameLocationY = "5";

		String d = "// Place local declarations here.\n";
		declarations.add(d);

		l = new UppaalLocation("id22", "-476", "68", "Irreversible_Failure", "-467", "85", false);

		locations.add(l);

		l = new UppaalLocation("id23", "-272", "68", "Emergency_Landing", "-255", "51", false);

		locations.add(l);

		l = new UppaalLocation("id24", "-476", "-204", "Limitted_Operation", "-493", "-255", false);

		locations.add(l);

		l = new UppaalLocation("id25", "-476", "-68", "Emergency_Mode", "-459", "-76", false);

		locations.add(l);

		l = new UppaalLocation("id26", "-102", "136", "Shutdown", "-195", "119", false);

		locations.add(l);

		l = new UppaalLocation("id27", "-102", "-68", "Mission_Completed", "-238", "-102", false);

		locations.add(l);

		l = new UppaalLocation("id28", "-272", "-68", "Regular_Landing", "-399", "-93", false);

		locations.add(l);
		
		l = new UppaalLocation("id29", "-612", "-68", "In_Flight", "-697", "-93", false);

		locations.add(l);

		l = new UppaalLocation("id30", "-748", "-68", "Take_off", "-833", "-76", true);

		locations.add(l);

		l = new UppaalLocation("id31", "-748", "-204", "Load_Mission", "-867", "-212", true);

		locations.add(l);

		l = new UppaalLocation("id32", "-748", "-272", "Idle", "-758", "-306", true);

		locations.add(l);
		
		this.init = "id32";

		// t1
		t = new UppaalTransition("id25", "id23");
		label = new UppaalLabel("flightCompleted()", GUARD, "-433", "51");
		t.addLabel(label);

		n = new UppaalNail("-442", "-34");

		t.addNail(n);

		n = new UppaalNail("-442", "68");

		t.addNail(n);

		transitions.add(t);

		// t2
		t = new UppaalTransition("id24", "id22");
		label = new UppaalLabel("checkOpt(FAIL)", GUARD, "-620", "102");
		t.addLabel(label);

		label = new UppaalLabel("updateSState(3)", ASSIG, "-620", "119");
		t.addLabel(label);

		n = new UppaalNail("-510", "-238");
		t.addNail(n);

		n = new UppaalNail("-646", "-238");
		t.addNail(n);

		n = new UppaalNail("-646", "102");
		t.addNail(n);

		n = new UppaalNail("-510", "102");
		t.addNail(n);

		transitions.add(t);
				
		// t3
		t = new UppaalTransition("id24", "id25");
		label = new UppaalLabel("checkOpt(EMER)", GUARD, "-433", "-161");
		t.addLabel(label);

		label = new UppaalLabel("updateSState(2)", ASSIG, "-433", "-144");
		t.addLabel(label);

		n = new UppaalNail("-442", "-170");
		t.addNail(n);

		n = new UppaalNail("-442", "-102");
		t.addNail(n);

		transitions.add(t);

		// t4
		t = new UppaalTransition("id25", "id24");
		label = new UppaalLabel("checkOpt(PART)", GUARD, "-603", "-161");
		t.addLabel(label);

		label = new UppaalLabel("updateSState(1)", ASSIG, "-595", "-144");
		t.addLabel(label);
	
		transitions.add(t);	
		
		// t5
		t = new UppaalTransition("id25", "id29");
		label = new UppaalLabel("checkOpt(REG)", GUARD, "-603", "34");
		t.addLabel(label);

		label = new UppaalLabel("updateSState(0)", ASSIG, "-603", "-17");
		t.addLabel(label);
		
		n = new UppaalNail("-510", "-34");
		t.addNail(n);

		n = new UppaalNail("-578", "-34");
		t.addNail(n);
		
		transitions.add(t);
		
		// t6
		t = new UppaalTransition("id24", "id29");
		label = new UppaalLabel("checkOpt(REG)", GUARD, "-629", "-238");
		t.addLabel(label);
	
		label = new UppaalLabel("updateSState(0)", ASSIG, "-637", "-255");
		t.addLabel(label);		

		n = new UppaalNail("-510", "-238");
		t.addNail(n);

		n = new UppaalNail("-646", "-238");
		t.addNail(n);

		n = new UppaalNail("-646", "-102");
		t.addNail(n);
		
		transitions.add(t);	
		
		// t7
		t = new UppaalTransition("id24", "id23");
		label = new UppaalLabel("!checkOpt(EMER) && !checkOpt(FAIL)", GUARD, "-416", "-238");
		t.addLabel(label);

		n = new UppaalNail("-238", "-204");
		t.addNail(n);

		n = new UppaalNail("-238", "34");
		t.addNail(n);

		transitions.add(t);	
	
		// t8
		t = new UppaalTransition("id23", "id27");
		label = new UppaalLabel("missionFinished()", GUARD, "-238", "76");
		t.addLabel(label);

		n = new UppaalNail("-102", "68");
		t.addNail(n);

		transitions.add(t);		
	
		// t9
		t = new UppaalTransition("id22", "id26");
		n = new UppaalNail("-476", "153");
		t.addNail(n);

		n = new UppaalNail("-136", "153");
		t.addNail(n);

		transitions.add(t);		
	
		// t10
		t = new UppaalTransition("id22", "id23");
		n = new UppaalNail("-476", "136");
		t.addNail(n);

		n = new UppaalNail("-306", "136");
		t.addNail(n);

		n = new UppaalNail("-306", "102");
		t.addNail(n);

		transitions.add(t);		
	
		// t11
		t = new UppaalTransition("id29", "id22");
		label = new UppaalLabel("checkOpt(FAIL)", GUARD, "-612", "51");
		t.addLabel(label);

		label = new UppaalLabel("updateSState(3)", ASSIG, "-629", "68");
		t.addLabel(label);

		n = new UppaalNail("-612", "68");
		t.addNail(n);

		transitions.add(t);		

		// t12
		t = new UppaalTransition("id25", "id22");
		label = new UppaalLabel("checkOpt(FAIL)", GUARD, "-595", "8");
		t.addLabel(label);

		label = new UppaalLabel("updateSState(3)", ASSIG, "-595", "25");
		t.addLabel(label);

		transitions.add(t);		
	
		// t13
		t = new UppaalTransition("id27", "id26");
		n = new UppaalNail("-85", "-34");
		t.addNail(n);
		
		n = new UppaalNail("-85", "102");
		t.addNail(n);

		transitions.add(t);	
	
		// t14
		t = new UppaalTransition("id27", "id32");
		label = new UppaalLabel("initSys = 0, mLoaded = 0, startMission = 0", ASSIG, "-544", "-297");
		t.addLabel(label);

		n = new UppaalNail("-102", "-272");
		t.addNail(n);

		transitions.add(t);	
		
		// t15
		t = new UppaalTransition("id23", "id32");
		label = new UppaalLabel("initSys = 0, mLoaded = 0, startMission = 0", ASSIG, "-790", "153");
		t.addLabel(label);

		n = new UppaalNail("-272", "170");
		t.addNail(n);

		n = new UppaalNail("-799", "170");
		t.addNail(n);

		n = new UppaalNail("-799", "-272");
		t.addNail(n);

		transitions.add(t);	
		
		// t16
		t = new UppaalTransition("id23", "id26");
		n = new UppaalNail("-238", "136");
		t.addNail(n);

		n = new UppaalNail("-119", "136");
		t.addNail(n);

		transitions.add(t);	
		
		// t17
		t = new UppaalTransition("id24", "id28");
		label = new UppaalLabel("flightCompleted()", GUARD, "-399", "-187");
		t.addLabel(label);

		n = new UppaalNail("-442", "-170");
		t.addNail(n);

		n = new UppaalNail("-272", "-170");
		t.addNail(n);

		transitions.add(t);	
		
		// t18
		t = new UppaalTransition("id28", "id27");
		
		transitions.add(t);	

		// t19
		t = new UppaalTransition("id25", "id28");
		label = new UppaalLabel("flightCompleted()", GUARD, "-408", "-34");
		t.addLabel(label);

		n = new UppaalNail("-459", "-17");
		t.addNail(n);

		n = new UppaalNail("-272", "-17");
		t.addNail(n);

		transitions.add(t);	
			
		// t20
		t = new UppaalTransition("id25", "id23");
		label = new UppaalLabel("!checkOpt(FAIL) && !checkOpt(PART) && !!checkOpt(REG)", GUARD, "-399", "-17");
		t.addLabel(label);

		n = new UppaalNail("-408", "-34");
		t.addNail(n);

		n = new UppaalNail("-408", "34");
		t.addNail(n);

		n = new UppaalNail("-306", "34");
		t.addNail(n);

		transitions.add(t);	

		// t21
		t = new UppaalTransition("id29", "id25");
		label = new UppaalLabel("checkOpt(EMER)", GUARD, "-595", "-85");
		t.addLabel(label);

		label = new UppaalLabel("updateSState(2)", ASSIG, "-595", "-59");
		t.addLabel(label);
		transitions.add(t);
		
		// t22
		t = new UppaalTransition("id29", "id24");
		label = new UppaalLabel("checkOpt(PART)", GUARD, "-603", "-195");
		t.addLabel(label);

		label = new UppaalLabel("updateSState(1)", ASSIG, "-620", "-221");
		t.addLabel(label);

		n = new UppaalNail("-612", "-204");
		t.addNail(n);
		
		transitions.add(t);	
				
		// t23
		t = new UppaalTransition("id29", "id28");
		label = new UppaalLabel("flightCompleted()", GUARD, "-425", "-119");
		t.addLabel(label);

		n = new UppaalNail("-578", "-102");
		t.addNail(n);

		n = new UppaalNail("-306", "-102");
		t.addNail(n);

		transitions.add(t);	
				
		// t24
		t = new UppaalTransition("id30", "id29");
		label = new UppaalLabel("start()", GUARD, "-705", "-59");
		t.addLabel(label);

		label = new UppaalLabel("startMission = 1, flightCount = 0", ASSIG, "-739", "-42");
		t.addLabel(label);
		transitions.add(t);	
				
		// t25
		t = new UppaalTransition("id31", "id30");
		label = new UppaalLabel("missionLoaded()", GUARD, "-875", "-170");
		t.addLabel(label);

		label = new UppaalLabel("mLoaded = 1", ASSIG, "-858", "-144");
		t.addLabel(label);
		transitions.add(t);	
		
		// t26
		t = new UppaalTransition("id32", "id31");
		label = new UppaalLabel("initSystem()", ASSIG, "-739", "-255");
		t.addLabel(label);

		transitions.add(t);	
	}
	
	
	public String getLocationId(String name) {
		for (UppaalLocation l : locations) {
			if (l.getName().equalsIgnoreCase(name))
				return l.getId();
		}
		return null;
	}

	public boolean searchLocationByName(String name){
		for(UppaalLocation l : locations){
			if(l.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public ArrayList<UppaalBranchpoint> getBranchpoints() {
		return branchpoints;
	}

	public String getLocationName(String id){
		for(UppaalLocation l : locations){
			if(l.getId().equalsIgnoreCase(id))
				return l.getName();
		}
		return "";
	}
}
