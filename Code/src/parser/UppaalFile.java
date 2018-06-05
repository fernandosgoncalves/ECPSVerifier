package parser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;

public class UppaalFile {
	private ArrayList<UppaalVariable> variables;
	private ArrayList<UppaalFunction> functions;
	private ArrayList<UppaalTemplate> templates;
	private ArrayList<String> declarations;
	private ArrayList<String> uppaalSystem;
	private ArrayList<UppaalQuery> queries;

	private String root;

	private int tasksId;
	private int tasksSize;

	UppaalFile() {
		uppaalSystem = new ArrayList<>();
		declarations = new ArrayList<>();
		templates = new ArrayList<>();
		functions = new ArrayList<>();
		variables = new ArrayList<>();
		queries = new ArrayList<>();

		declarations.add("// Place global declarations here.\n");

		uppaalSystem.add("// Place template instantiations here.\n");
		uppaalSystem.add("// List one or more processes to be composed into a system.\n");

		this.tasksId = 1;
		this.root = "nta";
	}

	public void incrementId() {
		tasksId++;
	}

	public int getId() {
		return this.tasksId;
	}

	public void addTemplate(UppaalTemplate t) {
		templates.add(t);
	}

	public ArrayList<UppaalVariable> getVariables() {
		return variables;
	}

	public void setVariableInitValue(String name, String value){
		//System.out.println("init Uppaal var value " + name + " " + value);
		for(UppaalVariable v : variables){
			if(v.getName().equalsIgnoreCase(name)){
				v.setInitialValue(value);
				//System.out.println("added");
			}
		}
	}
	
	public void setTasksTemplatesProperties(int behavior) {
		UppaalVariable tasks = new UppaalVariable("N", "int", "");
		tasks.setConstant(true);
		tasks.setInitialValue(Integer.toString(templates.size()));
		tasks.setComment("Number of tasks");

		tasksSize = templates.size();

		// variables.add(tasks);

		UppaalVariable ids = new UppaalVariable("id", "int", "");
		ids.setVector(true);
		ids.setComment("Process Ids");
		ids.setSize(Integer.valueOf(tasks.getInitialValue()));
		// ids.setTypedef(true);

		UppaalVariable len = new UppaalVariable("len", "int", tasks.getInitialValue());
		// len.setVector(true);
		// len.setSize(Integer.valueOf(tasks.getInitialValue()));
		// len.setInitialValue(tasks.getInitialValue());

		UppaalVariable priority = new UppaalVariable("P", "int", "");
		priority.setVector(true);
		priority.setConstant(true);
		priority.setComment("Priorities");
		priority.setSize(Integer.valueOf(tasks.getInitialValue()));

		UppaalVariable deadline = new UppaalVariable("D", "int", "");
		deadline.setVector(true);
		deadline.setConstant(true);
		deadline.setComment("Deadlines");
		deadline.setSize(Integer.valueOf(tasks.getInitialValue()));

		UppaalVariable readyB = new UppaalVariable("E", "int", "");
		readyB.setVector(true);
		readyB.setConstant(true);
		readyB.setComment("Ready Begin");
		readyB.setSize(Integer.valueOf(tasks.getInitialValue()));

		UppaalVariable readyE = new UppaalVariable("L", "int", "");
		readyE.setVector(true);
		readyE.setConstant(true);
		readyE.setComment("Ready End");
		readyE.setSize(Integer.valueOf(tasks.getInitialValue()));

		UppaalVariable computation = new UppaalVariable("C", "int", "");
		computation.setVector(true);
		computation.setConstant(true);
		computation.setComment("Ready End");
		computation.setSize(Integer.valueOf(tasks.getInitialValue()));

		if (behavior > 0) {
			UppaalVariable tState = new UppaalVariable("tState", "int", "");
			tState.setVector(true);
			tState.setSize(Integer.valueOf(tasks.getInitialValue()));
			for (int i = 0; i < Integer.valueOf(tasks.getInitialValue()); i++) {
				tState.addValue("0");
			}
			this.variables.add(tState);
		}

		for (UppaalTemplate t : templates) {
			// System.out.println(t.getTemplateId());
			ids.addValue(Integer.toString(t.getTemplateId()));

			priority.addValue(Integer.toString(t.getPriority()));

			deadline.addValue(Integer.toString(t.getDeadline()));

			readyB.addValue(Integer.toString(t.getPeriod()));

			readyE.addValue(Integer.toString(t.getPeriod()));

			computation.addValue(Integer.toString(t.getComputationTime()));
		}

		this.variables.add(tasks);
		this.variables.add(ids);
		this.variables.add(len);
		this.variables.add(priority);
		this.variables.add(deadline);
		this.variables.add(readyB);
		this.variables.add(readyE);
		this.variables.add(computation);

	}

	public void insertSchedulerDeclarations() {
		// TODO: Rever os canais para sensores e atuadores, acho que será unico
		// para eles

		// for(UppaalVariable v : variables){
		if (existsVariable("run") == -1) {
			UppaalVariable tempv = new UppaalVariable("run", "chan", "");
			tempv.setBroadcast(true);
			variables.add(tempv);
		}

		if (existsVariable("done") == -1) {
			UppaalVariable tempv = new UppaalVariable("done", "chan", "");
			tempv.setBroadcast(true);
			variables.add(tempv);
		}

		if (existsVariable("stop") == -1) {
			UppaalVariable tempv = new UppaalVariable("stop", "chan", "");
			tempv.setBroadcast(true);
			variables.add(tempv);
		}

		if (existsVariable("ready") == -1) {
			UppaalVariable tempv = new UppaalVariable("ready", "chan", "");
			tempv.setBroadcast(true);
			variables.add(tempv);
		}

		declarations.add("clock time;\n\n");

		if (existsVariable("errorOcr") == -1) {
			variables.add(new UppaalVariable("errorOcr", "int", "-1"));
		} else {
			int index = existsVariable("errorOcr");

			variables.get(index).setInitialValue("-1");
		}

		declarations.add("// Task queue\n");

		UppaalVariable queue = new UppaalVariable("queue", "int", "");
		queue.setSize(tasksSize);
		queue.setVector(true);

		variables.add(queue);

		declarations.add("int head() { return queue[0]; } //Get the top of the queue element\n\n");

		declarations.add("bool isEmpty() { return len == 0; } //Analyse if the queue is empty\n\n");

		declarations.add("void initializeSched(){\n");
		declarations.add("   // Bubble-sort tasks into the queue.\n");
		declarations.add("   bool picked[N];\n");
		declarations.add("   int i, j;\n");
		declarations.add("   for(i = 0; i < N; i++){\n");
		declarations.add("      int max = -1, t = -1;\n");
		declarations.add("      for(j = 0; j < N; j++){\n");
		declarations.add("         if (!picked[j] && P[j] > max){\n");
		declarations.add("            max = P[j];\n");
		declarations.add("            t = id[j];\n");
		declarations.add("         }\n");
		declarations.add("      }\n");
		declarations.add("      picked[t-1] = true;\n");
		declarations.add("      queue[i] = t;\n");
		declarations.add("   }\n");
		declarations.add("}\n\n");

		declarations.add("void add(int id){\n");
		declarations.add("   //Add a new schedueled task to the ordered queue\n");
		declarations.add("   int i, temp;\n");
		declarations.add("   queue[len] = id;\n");
		declarations.add("   for(i = len; i > 0 && P[queue[i]] > P[queue[i-1]]; i--){\n");
		declarations.add("      temp = queue[i];\n");
		declarations.add("      queue[i] = queue[i-1];\n");
		declarations.add("      queue[i-1] = temp;\n");
		declarations.add("   }\n");
		declarations.add("   len++;\n");
		declarations.add("}\n\n");

		declarations.add("void remove(){\n");
		declarations.add("   //remove the queue head\n");
		declarations.add("   int i;\n");
		declarations.add("   for(i = 0; i+1 < N; ++i) { queue[i] = queue[i+1]; }\n");
		declarations.add("   queue[--len] = 0;\n");
		declarations.add("}\n\n");
	}

	public void insertBehaviorDeclarations() {
		if (existsVariable("regular") == -1) {
			UppaalVariable tempv = new UppaalVariable("REG", "int", "0");
			tempv.setConstant(true);
			variables.add(tempv);
		}

		if (existsVariable("partial") == -1) {
			UppaalVariable tempv = new UppaalVariable("PART", "int", "1");
			tempv.setConstant(true);
			variables.add(tempv);
		}

		if (existsVariable("emergency") == -1) {
			UppaalVariable tempv = new UppaalVariable("EMER", "int", "2");
			tempv.setConstant(true);
			variables.add(tempv);
		}

		if (existsVariable("failure") == -1) {
			UppaalVariable tempv = new UppaalVariable("FAIL", "int", "3");
			tempv.setConstant(true);
			variables.add(tempv);
		}

		if (existsVariable("initsys") == -1) {
			UppaalVariable tempv = new UppaalVariable("initSys", "int", "0");
			variables.add(tempv);
		}

		if (existsVariable("schedinit") == -1) {
			UppaalVariable tempv = new UppaalVariable("schedInit", "int", "0");
			variables.add(tempv);
		}

		if (existsVariable("mLoaded") == -1) {
			UppaalVariable tempv = new UppaalVariable("mLoaded", "int", "0");
			variables.add(tempv);
		}

		if (existsVariable("startmission") == -1) {
			UppaalVariable tempv = new UppaalVariable("startMission", "int", "true");
			variables.add(tempv);
		}

		if (existsVariable("flightCount") == -1) {
			UppaalVariable tempv = new UppaalVariable("flightCount", "int", "0");
			variables.add(tempv);
		}

		if (existsVariable("mMode") == -1) {
			UppaalVariable tempv = new UppaalVariable("mMode", "int", "0");
			tempv.setComment("0 - Regular 1 - Partial 2 - Emergency 3 - Failure");
			variables.add(tempv);
		}

		declarations.add("void initSystem() { initSys = 1; } \n\n");

		declarations.add("bool systemInitialized() { return initSys; }\n\n");

		declarations.add("bool missionLoaded() { return schedInit; }\n\n");

		declarations.add("bool mStarted(){return startMission;}\n\n");

		declarations.add("bool start(){return true;}\n\n");

		declarations.add("bool checkOpt(int cState){\n");
		declarations.add("   int i;\n");
		declarations.add("   int tempState = 0;\n");
		declarations.add("   for(i=0; i < N; i++){\n");
		declarations.add("      if(tState[i] > tempState)\n");
		declarations.add("         tempState = tState[i];\n");
		declarations.add("   }\n\n");
		declarations.add("   if(tempState == cState)\n");
		declarations.add("      return true;\n");
		declarations.add("   else\n");
		declarations.add("      return false;\n");
		declarations.add("}\n\n");

		declarations.add("void updateSState(int newState){\n");
		declarations.add("   if(mMode < newState){\n");
		declarations.add("      mMode = newState;\n");
		declarations.add("   }else{\n");
		declarations.add("      if(newState == REG && mMode == PART){\n");
		declarations.add("         mMode = newState;\n");
		declarations.add("      }else{\n");
		declarations.add("         if(mMode == EMER && newState == REG && checkOpt(PART) && !checkOpt(EMER)){\n");
		declarations.add("            mMode = PART;\n");
		declarations.add("         }else{\n");
		declarations.add("            if(checkOpt(EMER) && newState != FAIL){\n");
		declarations.add("               mMode = EMER;\n");
		declarations.add("            }else{\n");
		declarations.add("               mMode = newState;\n");
		declarations.add("            }\n");
		declarations.add("         }\n");
		declarations.add("      }\n");
		declarations.add("   }\n");
		declarations.add("}\n\n");

		declarations.add("void upTState(int id, int newState){ tState[id] = newState; }\n\n");

		declarations.add("bool flightCompleted(){\n");
		declarations.add("    return flightCount >= 10;\n");
		declarations.add("}\n\n");		

		declarations.add("bool checkTState(int id,int stateId){\n");
		declarations.add("   return tState[id] == stateId;\n");		
		declarations.add("}\n\n");
		
		declarations.add("bool checkSystemState(){\n");		
		declarations.add("   int i;\n");		
		declarations.add("   int tempState = 0;\n");		
		declarations.add("   for(i=0; i < N; i++){\n");		
		declarations.add("      if(tState[i] > tempState)\n");		
		declarations.add("         tempState = tState[i];\n");		
		declarations.add("   }\n\n");
		declarations.add("   if(tempState == mMode)\n");
		declarations.add("      return true;\n");
		declarations.add("   else\n");
		declarations.add("      return false;\n");
		declarations.add("}\n\n");
		    
		declarations.add("bool missionFinished(){return flightCompleted();}\n\n");
	}

	public int existsVariable(String name) {
		for (UppaalVariable v : this.variables) {
			if (v.getName().equalsIgnoreCase(name))
				return variables.indexOf(v);
		}
		return -1;
	}

	public void addVariable(UppaalVariable v) {
		this.variables.add(v);
	}

	public void addFunction(UppaalFunction f) {
		this.functions.add(f);
	}

	public String getRoot() {
		return this.root;
	}

	protected String generateVariable(UppaalVariable v) {
		String var = "";

		if (v.isConstant())
			var += "const ";

		if (v.isBroadcast())
			var += "broadcast ";

		if (v.isTypedef())
			var += "typedef ";
		else
			var += v.getStringType() + " ";

		if (v.isVector()) {
			if (!v.getInitialValue().isEmpty()) {
				var += v.getName() + "[" + v.getSize() + "] ";
				var += "= " + v.getInitialValue() + ";";
			} else {
				var += v.getName() + "[" + v.getSize() + "]";

				if (v.getValues().size() > 0) {
					var += " = {";
					for (String s : v.getValues()) {
						var += s;
						if (v.getValues().size() > 1)
							var += ",";
					}
					var += "};";
				} else
					var += ";";
			}
		} else {
			var += v.getName();
			if (v.getInitialValue().length() > 0)
				var += " = " + v.getInitialValue() + ";";
			else
				var += ";";
		}

		if (!v.getComment().isEmpty())
			var += " //" + v.getComment();

		var += "\n\n";

		return var;
	}

	public void generateFile(String output) {
		/*
		 * TODO: Create the parser rules - Create the transformation rules
		 * TODO: Define the Device guards and updates 
		 */

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			DOMImplementation domImpl = doc.getImplementation();

			// XML root element
			Element rootElement = doc.createElement(root);
			doc.appendChild(rootElement);

			// XML declaration element
			Element declaration = doc.createElement("declaration");

			declaration.appendChild(doc.createTextNode(declarations.get(0)));
			declarations.remove(0);

			for (UppaalVariable v : this.variables) {
				declaration.appendChild(doc.createTextNode(generateVariable(v)));
			}

			for (String dec : declarations) {
				declaration.appendChild(doc.createTextNode(dec));
			}

			rootElement.appendChild(declaration);

			// XML template element
			for (UppaalTemplate t : templates) {
				Element template = doc.createElement("template");
				Element name = doc.createElement("name");
				name.setAttribute("x", t.getNameLocationX());
				name.setAttribute("y", t.getNameLocationY());
				name.setTextContent(t.getName());

				template.appendChild(name);

				Element tempDeclaration = doc.createElement("declaration");
				for (String d : t.getDeclarations())
					tempDeclaration.setTextContent(d);

				if (t.getTemplateId() > 0)
					tempDeclaration.appendChild(doc.createTextNode("const int id = " + t.getTemplateId() + ";\n\n"));

				for (UppaalVariable v : t.getVariables())
					tempDeclaration.appendChild(doc.createTextNode(generateVariable(v)));

				template.appendChild(tempDeclaration);

				for (UppaalLocation l : t.getLocations()) {
					// System.out.println("Location " + l.getName() + " " +
					// l.getId());
					Element tempLocation = doc.createElement("location");
					tempLocation.setAttribute("id", l.getId());
					tempLocation.setAttribute("x", l.getX());
					tempLocation.setAttribute("y", l.getY());

					Element locationName = doc.createElement("name");
					locationName.setAttribute("x", l.getNameX());
					locationName.setAttribute("y", l.getNameY());
					locationName.appendChild(doc.createTextNode(l.getName()));
					tempLocation.appendChild(locationName);

					for (UppaalLabel label : l.getLabels()) {
						Element labelT = doc.createElement("label");
						labelT.setAttribute("kind", label.getKind());
						labelT.setAttribute("x", label.getX());
						labelT.setAttribute("y", label.getY());
						labelT.appendChild(doc.createTextNode(label.getExpression()));
						tempLocation.appendChild(labelT);
					}

					if (l.isCommited()) {
						Element commit = doc.createElement("committed");
						tempLocation.appendChild(commit);
					}

					template.appendChild(tempLocation);

				}

				if (!t.getBranchpoints().isEmpty()) {
					for (UppaalBranchpoint b : t.getBranchpoints()) {
						Element tempBranchpoint = doc.createElement("branchpoint");

						// System.out.println("bId " + b.getId() + " " +
						// b.getLocationId());

						tempBranchpoint.setAttribute("id", b.getId());
						tempBranchpoint.setAttribute("x", b.getX());
						tempBranchpoint.setAttribute("y", b.getY());

						tempBranchpoint.appendChild(doc.createTextNode("\n"));

						template.appendChild(tempBranchpoint);

					}
				}

				Element tempInit = doc.createElement("init");
				tempInit.setAttribute("ref", t.getInit());

				template.appendChild(tempInit);

				for (UppaalTransition trans : t.getTransitions()) {
					Element tempTransition = doc.createElement("transition");

					Element source = doc.createElement("source");
					Element target = doc.createElement("target");

					Element auxTELabel = null;

					//System.out.println("T Name: " + t.getName() + " Transições: " + t.getTransitions().size());
					//System.out.println("Source: " + trans.getSource() + " Target: " + trans.getTarget());
					//System.out.println("Branchpoints: " + t.getBranchpoints().size());
					
					if ((!t.searchLocationBranchpoint(trans.getTarget()).isEmpty() && t.searchLocationBranchpoint(trans.getSource()).isEmpty())  || trans.getTarget().equals(t.getInit())) {
						//System.out.println("Here 1");
						source.setAttribute("ref", trans.getSource());
						target.setAttribute("ref", trans.getTarget());
					} else {
						if (!t.getBranchpoints().isEmpty()) {
							if (!t.searchLocationBranchpoint(trans.getSource()).isEmpty()) {
								//System.out.println("Here 2");
								source.setAttribute("ref", t.searchLocationBranchpoint(trans.getSource()));

								auxTELabel = doc.createElement("label");
								auxTELabel.setAttribute("kind", "assignment");
								auxTELabel.setAttribute("x", "0");
								auxTELabel.setAttribute("y", "0");
								auxTELabel.appendChild(doc.createTextNode("errorOcr=1"));
							} else {
								//System.out.println("Here 3");
								source.setAttribute("ref", trans.getSource());
							}
							target.setAttribute("ref", trans.getTarget());
						} else {
							//System.out.println("Here 4");
							source.setAttribute("ref", trans.getSource());
							target.setAttribute("ref", trans.getTarget());
						}
					}

					tempTransition.appendChild(source);
					tempTransition.appendChild(target);

					if (auxTELabel != null)
						tempTransition.appendChild(auxTELabel);

					// label
					for (UppaalLabel tempLabel : trans.getLabels()) {
						Element auxLabel = doc.createElement("label");

						auxLabel.setAttribute("kind", tempLabel.getKind());
						auxLabel.setAttribute("x", tempLabel.getX());
						auxLabel.setAttribute("y", tempLabel.getY());
						auxLabel.appendChild(doc.createTextNode(tempLabel.getExpression()));

						tempTransition.appendChild(auxLabel);
					}

					// Nail
					for (UppaalNail tempNail : trans.getNails()) {
						Element auxNail = doc.createElement("nail");

						auxNail.setAttribute("x", tempNail.getX());
						auxNail.setAttribute("y", tempNail.getY());

						tempTransition.appendChild(auxNail);
					}

					template.appendChild(tempTransition);
				}

				if (!t.getBranchpoints().isEmpty()) {
					for (UppaalBranchpoint b : t.getBranchpoints()) {
						Element tempTransition = doc.createElement("transition");

						Element source = doc.createElement("source");
						Element target = doc.createElement("target");

						source.setAttribute("ref", b.getLocationId());
						target.setAttribute("ref", b.getId());

						tempTransition.appendChild(source);
						tempTransition.appendChild(target);

						template.appendChild(tempTransition);
					}
				}

				rootElement.appendChild(template);
			}

			// XML System element
			Element system = doc.createElement("system");

			String sys = "system ";
			int i = 1;
			for (UppaalTemplate temp : templates) {
				sys += temp.getName();
				if (i++ != templates.size()) {
					sys += ", ";
				}
			}
			sys += ";\n";
			uppaalSystem.add(sys);

			for (String s : uppaalSystem)
				system.appendChild(doc.createTextNode(s));

			rootElement.appendChild(system);

			// XML queries element
			Element queriesUppaal = doc.createElement("queries");
			if (queries.isEmpty())
				queriesUppaal.appendChild(doc.createTextNode("\n"));
			else {
				for (UppaalQuery q : queries) {
					Element query = doc.createElement("query");
					Element formula = doc.createElement("formula");
					formula.setTextContent(q.getFormula());

					Element comment = doc.createElement("comment");
					if (!q.getComment().isEmpty())
						comment.setTextContent(q.getComment());
					else
						comment.setTextContent("\n");
					query.appendChild(formula);
					query.appendChild(comment);
					queriesUppaal.appendChild(query);
				}
			}

			rootElement.appendChild(queriesUppaal);

			// Document generation
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");

			DocumentType doctype = domImpl.createDocumentType("nta", "-//Uppaal Team//DTD Flat System 1.1//EN",
					"http://www.it.uu.se/research/group/darts/uppaal/flat-1_2.dtd");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new java.io.File(output));
			transformer.transform(source, result);
			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}
