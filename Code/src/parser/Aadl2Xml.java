package parser;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.FileReader;

public class Aadl2Xml {

	private UppaalFile uppaalFile;

	private AadlFile aadlFile;

	private BufferedReader bfReader;

	private String fileName;
	private String path;

	private static final String SUBPROGRAM = "subprogram";
	private static final String PROCESS = "process";
	private static final String SYSTEM = "system";
	private static final String DEVICE = "device";
	private static final String THREAD = "thread";
	private static final String ANNEX = "annex";
	private static final String DATA = "data";

	public Aadl2Xml(String file, String fileName, String path) throws Exception {
		/*
		 * Constructor
		 */

		// TODO Evaluate all the external function to include the base case on
		// the while loop (Look error propagation function)
		// TODO indexOf(char)
		// TODO http://www.graphviz.org

		super();

		uppaalFile = new UppaalFile();

		aadlFile = new AadlFile();

		this.fileName = fileName;

		this.path = path;

		try {
			bfReader = new BufferedReader(new FileReader(file));
			System.out.println("File opened");
		} catch (FileNotFoundException e) {
			throw e;
		}

		try {
			parse();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		try {
			// System.out.println("Transform");
			transformEngine();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void transformEngine() {
		/*
		 * Perform the transformation from AADL to UPPAAL file
		 */

		for (AadlThreadImpl t : aadlFile.getThreadsImpl()) {
			uppaalFile.addTemplate(generateThreadTemplate(t));
		}

		if (aadlFile.isDeviceCreated("behavior"))
			uppaalFile.setTasksTemplatesProperties(1);
		else
			uppaalFile.setTasksTemplatesProperties(0);

		for (AadlDeviceImpl d : aadlFile.getDevicesImpl()) {
			uppaalFile.addTemplate(generateDeviceTemplate(d));
		}
	}

	private void parse() throws Exception {
		/*
		 * This function perform the AADL file reading and map their structures to
		 * provide the automata generation
		 */
		String line;
		String name = "";
		ArrayList<String> component = new ArrayList<>();
		int componetInitialized = 0;

		try {
			while (bfReader.ready()) {
				line = bfReader.readLine();
				line = line.trim();
				// System.out.println(line);
				if (line.isEmpty()) {
					/*
					 * Ignore empty lines
					 */
					// System.out.println("ignored");
					continue;
				}
				if (line.substring(0, 2).equalsIgnoreCase("--")) {
					/*
					 * Ignore commented lines
					 */
					// System.out.println("ignored comment");
					continue;
				}
				if (ignoreLine(line)) {
					/*
					 * Ignore PACKAGE, PUBLIC and WITH lines
					 */
					// System.out.println("ignored");
					continue;
				}
				if (componetInitialized == 0 && componentInitialTag(line)) {
					// System.out.println("Begin " + line);
					name = getComponentName(line);
					// System.out.println(name);
					componetInitialized = 1;
					component.add(line);
					continue;
				}
				if (componetInitialized == 1 && endComponent(line, name)) {
					/*
					 * Analyze if the component is ended
					 */
					component.add(line);
					processComponent(component);
					componetInitialized = 0;
					component.clear();
					continue;
				}
				if (componetInitialized == 1 && !endComponent(line, name)) {
					/*
					 * Get the internal component lines
					 */
					component.add(line);
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void processComponent(ArrayList<String> component) {
		/*
		 * According with the component type a function its invoked to process it
		 */
		String line[] = component.get(0).split(" ");
		switch (line[0].toLowerCase()) {
		case SYSTEM:
			if (line[1].equalsIgnoreCase("implementation"))
				processSystemImplementation(component);
			else
				processSystem(component);
			break;
		case DEVICE:
			if (line[1].equalsIgnoreCase("implementation"))
				processDeviceImplementation(component);
			else
				processDevice(component);
			break;
		case PROCESS:
			if (line[1].equalsIgnoreCase("implementation"))
				processProcessImplementation(component);
			else
				processProcess(component);
			break;
		case SUBPROGRAM:
			processSubprogram(component);
			break;
		case THREAD:
			if (line[1].equalsIgnoreCase("implementation"))
				processThreadImplementation(component);
			else
				processThread(component);
			break;
		case ANNEX:
			processAnnexEmv2(component);
			break;
		}
		// TODO Evaluate if data components need to be mapped.

	}

	private void processAnnexEmv2(ArrayList<String> component) {
		/*
		 * Error annex mapping
		 */
		AadlErrorAnnexLibrary library = new AadlErrorAnnexLibrary();
		AadlErrorBehavior behaviorModel = null;

		String behaviorName = "";
		String stateName = "";

		boolean initial = false;

		int openBTransitions = -1;
		int openBehavior = -1;
		int openBEvents = -1;
		int openBStates = -1;
		int openTypes = -1;
		int pos = 0;

		//System.out.println("Error Annex");

		for (String l : component) {
			String line[] = l.split(" ");

			if (l.substring(0, 2).equalsIgnoreCase("--")) {
				// System.out.println("comment");
				// System.out.println(l);
				continue;
			}

			if (line[0].equalsIgnoreCase("error")) {
				if (line[1].equalsIgnoreCase("types")) {
					openTypes = 1;
					openBehavior = 0;
					continue;
				}

				if (line[1].equalsIgnoreCase("behavior")) {
					openTypes = 0;
					openBehavior = 1;
					behaviorName = line[2];
					// System.out.println("Error behavior Model: " +
					// behaviorName);
					openBEvents = 0;
					openBStates = 0;
					openBTransitions = 0;
					behaviorModel = new AadlErrorBehavior(behaviorName);
					continue;
				}
			}

			if (line[0].equalsIgnoreCase("end")) {
				if (openTypes == 1)
					openTypes = 0;

				if (openBehavior == 1) {
					library.addErrorBehaviorModel(behaviorModel);
					openBehavior = 0;
				}
				continue;
			}

			if (openTypes == 1) {
				// System.out.println("Type");
				if (getCharPositionInString(line[0], ":") > -1) {
					// System.out.println(line[0].substring(0,
					// getCharPositionInString(line[0], ":")));
					library.addErrorType(line[0].substring(0, getCharPositionInString(line[0], ":")));
				} else {
					// System.out.println(line[0]);
					library.addErrorType(line[0]);
				}
				continue;
			}

			if (openBehavior == 1) {
				if (line[0].equalsIgnoreCase("events")) {
					openBEvents = 1;
					openBStates = 0;
					openBTransitions = 0;
					continue;
				}

				if (line[0].equalsIgnoreCase("states")) {
					openBEvents = 0;
					openBStates = 1;
					openBTransitions = 0;
					continue;
				}

				if (line[0].equalsIgnoreCase("transitions")) {
					openBEvents = 0;
					openBStates = 0;
					openBTransitions = 1;
					continue;
				}

				if (openBEvents == 1) {
					// System.out.println("Behavior Event");
					if (getCharPositionInString(line[0], ":") > -1) {
						// System.out.println(line[0].substring(0,
						// getCharPositionInString(line[0], ":")));
						behaviorModel.addEvents(line[0].substring(0, getCharPositionInString(line[0], ":")));
					} else {
						// System.out.println(line[0]);
						behaviorModel.addEvents(line[0]);
					}
					continue;
				}

				if (openBStates == 1) {
					// System.out.println("Behavior State");
					initial = false;
					pos = 0;
					while (pos < line.length) {
						// System.out.println(line[pos]);
						if (getCharPositionInString(line[pos], ":") > -1) {
							if (line[pos].substring(getCharPositionInString(line[pos], ":") + 1)
									.equalsIgnoreCase("initial")) {
								initial = true;
								break;
							}
						} else {
							if (line[pos].equalsIgnoreCase("initial")) {
								initial = true;
								break;
							}
						}
						pos++;
					}

					if (getCharPositionInString(line[0], ":") > -1)
						stateName = line[0].substring(0, getCharPositionInString(line[0], ":"));
					else
						stateName = line[0];

					// System.out.println(stateName + " " + initial);
					behaviorModel.addState(new AadlState(stateName, initial, false, false));
				}

				if (openBTransitions == 1) {
					// System.out.println("Behavior Transition");
					// System.out.println(l);
					behaviorModel.addTransitions(generateTransition(l, true));
				}
			}
		}

		aadlFile.setAnnexEmv2(library);
	}

	private void processThread(ArrayList<String> component) {
		/*
		 * Thread generation
		 */
		String line[] = component.get(0).split(" ");
		int pos = 1;

		// System.out.println(" ");
		System.out.println("Processing a thread" + line[1]);

		AadlThread thread;
		thread = new AadlThread(line[1]);

		if (component.size() > 2) {
			if (component.get(pos).equalsIgnoreCase("features")) {
				pos++;
				while (pos < component.size()) {
					if (component.get(pos).length() >= 3) {
						if (component.get(pos).substring(0, 3).equalsIgnoreCase("end")) {
							pos++;
							continue;
						} else {
							if (!component.get(pos).substring(0, 2).equalsIgnoreCase("--"))
								thread.addFeature(generateFeature(component.get(pos)));
						}
					}
					pos++;
				}
			}
		}

		aadlFile.addThread(thread);
	}

	private void processThreadImplementation(ArrayList<String> component) {
		/*
		 * Thread implementation generation
		 */
		String line[] = component.get(0).split(" ");

		// System.out.println(" ");
		System.out.println("Processing a thread implementation " + line[2]);

		AadlThreadImpl tempThreadImpl;

		String subprogName;
		String callName;

		int transitions = 0;
		int properties = 0;
		int variables = 0;
		int states = 0;
		int annex = 0;
		int call = 0;
		int open = 0;
		int pos;

		String name[] = line[2].split("\\.");

		tempThreadImpl = new AadlThreadImpl(line[2], name[0]);

		int size = component.size();
		for (String l : component) {
			l.trim();

			if (l.substring(0, 2).equalsIgnoreCase("--")) {
				// System.out.println("comment");
				// System.out.println(l);
				continue;
			}
			if (l.equalsIgnoreCase("calls")) {
				// System.out.println("calls");
				call = 1;
				properties = 0;
				annex = 0;
				continue;
			}
			if (l.equalsIgnoreCase("properties")) {
				// System.out.println("properties");
				call = 0;
				properties = 1;
				annex = 0;
				continue;
			}
			if (l.length() >= 5) {
				if (l.toLowerCase().substring(0, 5).equals("annex")) {
					// System.out.println("annex");
					call = 0;
					properties = 0;
					annex = 1;
					continue;
				}
			}
			if (l.length() >= 3) {
				if (--size == 0 || l.substring(0, 3).equalsIgnoreCase("end")) {
					call = 0;
					properties = 0;
					annex = 0;
					continue;
				}
			}
			if (call == 1) {
				// System.out.println("call");
				// Include the thread calls
				pos = 0;
				if (l.length() > 0) {
					if (l.substring(l.length() - 1, l.length()).equals("{")) {
						// opening
						open = 1;
						continue;
					}
					if (l.substring(l.length() - 2, l.length()).equals("};")) {
						// ending
						open = 0;
						continue;
					}
				}

				if (open == 1) {
					String tempSCall[] = l.split(" ");

					if (!tempSCall[1].equals(":")) {
						pos = 1;
						callName = tempSCall[0].substring(0, tempSCall[0].length() - 1);
						subprogName = tempSCall[pos + 1].substring(0, tempSCall[pos + 1].length() - 1);
					} else {
						pos = 2;
						callName = tempSCall[1];
						subprogName = tempSCall[pos + 1].substring(0, tempSCall[pos + 1].length() - 1);
					}

					AadlCall tempCall = new AadlCall(callName, subprogName);
					// System.out.println(callName + " " + subprogName);
					tempThreadImpl.addCall(tempCall);
				}

			}
			if (properties == 1) {
				tempThreadImpl.addProperty(generateProperty(l));
			}
			if (annex == 1) {
				// System.out.println("behavior annex");
				// behavior annex
				String behavior[] = l.split(" ");
				if (l.length() > 0) {
					if (l.equalsIgnoreCase("variables")) {
						// System.out.println("Annex variables");
						variables = 1;
						states = 0;
						transitions = 0;
						continue;
					}
					if (l.equalsIgnoreCase("states")) {
						// System.out.println("Annex states");
						variables = 0;
						states = 1;
						transitions = 0;
						continue;
					}
					if (l.equalsIgnoreCase("transitions")) {
						// System.out.println("Annex transitions");
						variables = 0;
						states = 0;
						transitions = 1;
						continue;
					}
					if (l.substring(l.length() - 4, l.length()).equals("**};")) {
						// ending
						variables = 0;
						states = 0;
						transitions = 0;
						// System.out.println("Close");
						continue;
					}
					if (variables == 1) {
						// System.out.println("annex variable");
						// variables
						if (behavior[1].equals(":")) {
							tempThreadImpl.addVariable(
									new AadlVariable(behavior[0], behavior[2].substring(0, behavior[2].length() - 1)));
							// System.out.println(
							// behavior[0] + " " + behavior[2].substring(0,
							// behavior[2].length() - 1) + " added;");
						} else {
							tempThreadImpl
									.addVariable(new AadlVariable(behavior[0].substring(0, behavior[0].length() - 1),
											behavior[2].substring(0, behavior[2].length() - 1)));
							// System.out.println(behavior[0].substring(0,
							// behavior[0].length() - 1) + " "
							// + behavior[2].substring(0, behavior[2].length() -
							// 1) + " added;");
						}

					}
					if (states == 1) {
						// System.out.println("annex state");
						// states
						String stateName = null;

						boolean finalState = false;
						boolean complete = false;
						boolean initial = false;

						// if (openBStates == 1) {
						// initial1 = false;
						pos = 0;
						while (pos < behavior.length) {
							// System.out.println(line[pos]);
							if (getCharPositionInString(behavior[pos], ":") > -1) {
								if (behavior[pos].substring(getCharPositionInString(behavior[pos], ":") + 1)
										.equalsIgnoreCase("initial")) {
									initial = true;
								}
								if (behavior[pos].substring(getCharPositionInString(behavior[pos], ":") + 1)
										.equalsIgnoreCase("complete")) {
									complete = true;
								}
								if (behavior[pos].substring(getCharPositionInString(behavior[pos], ":") + 1)
										.equalsIgnoreCase("final")) {
									finalState = true;
								}
							} else {
								if (behavior[pos].equalsIgnoreCase("initial")) {
									initial = true;
								}
								if (behavior[pos].equalsIgnoreCase("complete")) {
									complete = true;
								}
								if (behavior[pos].equalsIgnoreCase("final")) {
									finalState = true;
								}
							}
							pos++;
						}

						if (getCharPositionInString(behavior[0], ":") > -1)
							stateName = behavior[0].substring(0, getCharPositionInString(behavior[0], ":"));
						else
							stateName = behavior[0];

						// System.out.println(stateName + " " + initial + " " +
						// complete + " " + finalState);
						// System.out.println(stateName + " " + initial + " " +
						// complete + " " + finalState);
						// System.out.println("");
						tempThreadImpl.addState(new AadlState(stateName, initial, complete, finalState));
					}
					if (transitions == 1) {
						// System.out.println("annex transition");
						// System.out.println(" ");
						// System.out.println(l);
						tempThreadImpl.addTransition(generateTransition(l, true));
					}
				}
			}
		}

		aadlFile.addThreadImpl(tempThreadImpl);

	}

	private void processSubprogram(ArrayList<String> component) {
		/*
		 * Subprogram processing
		 */
		AadlSubprogram tempSubprogram;

		int properties = 0;
		int features = 0;

		String line[] = component.get(0).split(" ");

		// System.out.println(" ");
		System.out.println("Processing a subprogram " + line[1]);

		tempSubprogram = new AadlSubprogram(line[1]);

		// Add the subcomponents of a subprogram
		int size = component.size();
		for (String l : component) {
			l.trim();
			if (l.equalsIgnoreCase("features")) {
				features = 1;
				properties = 0;
				continue;
			}
			if (l.equalsIgnoreCase("properties")) {
				features = 0;
				properties = 1;
				continue;
			}
			if (--size == 0 || l.substring(0, 3).equalsIgnoreCase("end")) {
				features = 0;
				properties = 0;
				continue;
			}
			if (features == 1) {
				// Include all the subprogram subcomponents
				tempSubprogram.addFeature(generateFeature(l));
			}

			if (properties == 1) {
				// Include all the subprogram properties
				tempSubprogram.addProperty(generateProperty(l));
			}
		}

		aadlFile.addSubprogram(tempSubprogram);

	}

	private void processProcess(ArrayList<String> component) {
		/*
		 * Process generation
		 */
		String line[] = component.get(0).split(" ");
		int pos = 1;

		// System.out.println(" ");
		System.out.println("Processing a process " + line[1]);
		AadlProcess process;
		process = new AadlProcess(line[1]);

		if (component.size() > 2) {
			if (component.get(pos).equalsIgnoreCase("features")) {
				pos++;
				while (pos < component.size()) {
					if (component.get(pos).length() >= 3) {
						if (component.get(pos).substring(0, 3).equalsIgnoreCase("end")) {
							pos++;
							continue;
						} else {
							if (!component.get(pos).substring(0, 2).equalsIgnoreCase("--"))
								process.addFeature(generateFeature(component.get(pos)));
						}
					}
					pos++;
				}
			}
		}

		aadlFile.addProcess(process);
	}

	private void processProcessImplementation(ArrayList<String> component) {
		/*
		 * Process implementation generation
		 */
		String line[] = component.get(0).split(" ");

		// System.out.println(" ");
		System.out.println("Processing a process implementation " + line[2]);
		AadlProcessImpl tempImpl;
		String scname;

		int subcomponent = 0;
		int connections = 0;
		int pos;

		String name[] = line[2].split("\\.");

		tempImpl = new AadlProcessImpl(line[2], name[0]);

		// Create or founded the main structure the additional structures of the
		// implementation are mapped.
		int size = component.size();
		for (String l : component) {
			l.trim();
			if (l.equalsIgnoreCase("subcomponents")) {
				subcomponent = 1;
				connections = 0;
				continue;
			}
			if (l.equalsIgnoreCase("connections")) {
				connections = 1;
				subcomponent = 0;
				continue;
			}
			if (--size == 0 || l.substring(0, 3).equalsIgnoreCase("end")) {
				connections = 0;
				subcomponent = 0;
				continue;
			}
			if (subcomponent == 1) {
				// Include all the process subcomponents
				pos = 0;
				String subcomp[] = l.split(" ");
				if (!subcomp[1].equals(":"))
					pos = 1;
				else
					pos = 2;

				if (pos == 1)
					scname = subcomp[0].substring(0, subcomp[0].length() - 2);
				else
					scname = subcomp[0];

				AadlSubcomponent tempSubcomponent = new AadlSubcomponent(scname,
						subcomp[pos + 1].substring(0, subcomp[pos + 1].length() - 1).toLowerCase(),
						subcomp[pos].toLowerCase());
				// System.out.println(subcomp[pos] + " "
				// + subcomp[pos + 1].substring(0, subcomp[pos + 1].length() -
				// 1).toLowerCase() + " added.");
				tempImpl.addSubcomponent(tempSubcomponent);
			}
			if (connections == 1) {
				// Include all the process connections
				pos = 0;
				String connect[] = l.split(" ");
				if (!connect[1].equals(":")) {
					pos = 1;
					scname = connect[0].substring(0, connect[0].length() - 1);
				} else {
					pos = 2;
					scname = connect[0];
				}

				// AadlConnection tempConnection = new AadlConnection(scname,
				// connect[pos].toLowerCase(),
				// connect[pos + 1].toLowerCase(), connect[pos + 3].substring(0,
				// connect[pos + 3].length() - 1));
				// System.out.println("Connection: " + scname + " : " +
				// connect[pos] + " " + connect[pos + 1] + " --> "
				// + connect[pos + 3].substring(0, connect[pos + 3].length() -
				// 1));
				tempImpl.addConnection(new AadlConnection(scname, connect[pos].toLowerCase(),
						connect[pos + 1].toLowerCase(), connect[pos + 3].substring(0, connect[pos + 3].length() - 1)));
			}
		}

		aadlFile.addProcessImpl(tempImpl);
	}

	private void processDevice(ArrayList<String> component) {
		/*
		 * Device creation
		 */
		AadlDevice device;
		String line[] = component.get(0).split(" ");
		int pos = 1;

		// System.out.println(" ");
		// System.out.println("Processing a device " + line[1]);
		device = new AadlDevice(line[1]);

		int features = 0;
		int properties = 0;

		if (component.size() > 2) {
			while (pos < component.size()) {
				if (component.get(pos).length() >= 3) {
					if (component.get(pos).equalsIgnoreCase("features")) {
						features = 1;
						properties = 0;
						pos++;
						continue;
					}
					if (component.get(pos).equalsIgnoreCase("properties")) {
						features = 0;
						properties = 1;
						pos++;
						continue;
					}
					if (component.get(pos).substring(0, 3).equalsIgnoreCase("end")) {
						features = 0;
						properties = 0;
						pos++;
						continue;
					}
					if (component.get(pos).substring(0, 2).equalsIgnoreCase("--")) {
						pos++;
						continue;
					}

					if (features == 1) {
						device.addFeature(generateFeature(component.get(pos)));
						pos++;
						continue;
					}
					if (properties == 1) {
						device.addProperty(generateProperty(component.get(pos)));
						pos++;
						continue;
					}
				} else {
					pos++;
				}
			}
		}

		aadlFile.addDevice(device);
	}

	private void processDeviceImplementation(ArrayList<String> component) {
		/*
		 * Parser of the device implementation
		 */
		int errorPropagations = 0;
		int errorTransitions = 0;
		int errorDetections = 0;
		int errorBehavior = 0;
		int properties = 0;
		int errorProp = 0;
		int annex = 0;

		AadlDeviceImpl tempDeviceImpl;
		AadlErrorAnnex errorAnnex = new AadlErrorAnnex();
		String line[] = component.get(0).split(" ");

		// System.out.println(" ");
		// System.out.println("Processing a device implementation " + line[2]);
		String name[] = line[2].split("\\.");

		// Create a new device implementation instance
		tempDeviceImpl = new AadlDeviceImpl(line[2], name[0]);

		// Create or founded the main structure the additional structures of the
		// implementation are mapped.
		int size = component.size();
		for (String l : component) {
			l = l.trim();
			// System.out.println(l);

			if (isErrorAnnex(l)) {
				annex = 1;
				continue;
			}

			if (--size == 0 || l.equals("**};")) {
				tempDeviceImpl.setErrorAnnex(errorAnnex);
				annex = 0;
				continue;
			}

			if (annex == 1) {
				//System.out.println("Error Annex Parser");

				if (l.length() > 0) {
					if (l.substring(0, 2).equalsIgnoreCase("--"))
						continue;

					String errorLine[] = l.split(" ");

					if (errorLine.length >= 2) {
						if (errorLine[0].equalsIgnoreCase("use")) {
							if (errorLine[1].equalsIgnoreCase("types")) {
								// System.out.println("Types " + errorLine[2].substring(0, errorLine[2].length()
								// - 1));
								errorAnnex.setTypes(errorLine[2].substring(0, errorLine[2].length() - 1));
								continue;
							}
							if (errorLine[1].equalsIgnoreCase("behavior")) {
								// System.out.println("behavior " + errorLine[2].substring(0,
								// errorLine[2].length() - 1));
								errorAnnex.setBehavior(errorLine[2].substring(0, errorLine[2].length() - 1));
								continue;
							}
						}

						if (errorLine[0].equalsIgnoreCase("error") && errorLine[1].equalsIgnoreCase("propagations")) {
							// System.out.println("Open propagation");
							errorProp = 1;
							errorBehavior = 0;
							properties = 0;
							continue;
						}

						if (errorLine[0].equalsIgnoreCase("end") && errorLine[1].equalsIgnoreCase("propagations;")) {
							// System.out.println("End propagations");
							errorProp = 0;
							continue;
						}

						if (errorLine[0].equalsIgnoreCase("component") && errorLine[1].equalsIgnoreCase("error")) {
							// System.out.println("Open component behavior");
							errorProp = 0;
							errorBehavior = 1;
							properties = 0;
							continue;
						}

						if (errorLine[0].equalsIgnoreCase("end") && errorLine[1].equalsIgnoreCase("component;")) {
							// System.out.println("End behavior");
							errorProp = 0;
							continue;
						}

					}

					if (errorLine[0].equalsIgnoreCase("properties")) {
						// System.out.println("Open properties");
						properties = 1;
						errorProp = 0;
						errorBehavior = 0;
						continue;
					}

					if (errorProp == 1) {
						// System.out.println("Propagation");
						errorAnnex.addErrorPropagation(generateErrorPropagation(l));
						continue;
					}

					if (errorBehavior == 1) {
						//System.out.println("Error behavior");

						if (errorLine[0].equalsIgnoreCase("end")) {
							// System.out.println("End component");
							errorPropagations = 0;
							errorTransitions = 0;
							errorDetections = 0;
							continue;
						}
						if (errorLine[0].equalsIgnoreCase("transitions")) {
							// System.out.println("transition");
							errorPropagations = 0;
							errorTransitions = 1;
							errorDetections = 0;
							continue;
						}
						if (errorLine[0].equalsIgnoreCase("propagations")) {
							// System.out.println("propagation");
							errorPropagations = 1;
							errorTransitions = 0;
							errorDetections = 0;
							continue;
						}
						if (errorLine[0].equalsIgnoreCase("detections")) {
							// System.out.println("detection");
							errorPropagations = 0;
							errorTransitions = 0;
							errorDetections = 1;
							continue;
						}

						if (errorTransitions == 1) {
							// System.out.println("Process transition: " + l);
							// errorAnnex.addTransition(generatePropagation(l));
							errorAnnex.addTransition(generateTransition(l, false));
							continue;
						}

						if (errorPropagations == 1) {
							// System.out.println("Process propagation");
							// errorAnnex.addPropagation(generatePropagation(l));
							errorAnnex.addPropagation(generateTransition(l, false));
							continue;
						}

						if (errorDetections == 1) {
							// System.out.println("Process detection");
							// errorAnnex.addDetection(generatePropagation(l));
							errorAnnex.addDetection(generateTransition(l, false));
							continue;
						}

					}

					if (properties == 1) {
						// System.out.println("Process property");
						errorAnnex.addProperty(generateErrorProperty(l));
					}
				}
			}
		}

		aadlFile.addDeviceImpl(tempDeviceImpl);

	}

	private void processSystem(ArrayList<String> component) {
		/*
		 * System creation
		 */
		AadlSystem system;
		String line[] = component.get(0).split(" ");

		System.out.println("Processing a system " + line[1]);
		if (!aadlFile.isHaveRoot())
			system = new AadlSystem(line[1], true);
		else
			system = new AadlSystem(line[1]);

		aadlFile.addSystem(system);
	}

	private void processSystemImplementation(ArrayList<String> component) {
		/*
		 * System implementation creation
		 */
		AadlSystemImpl tempImpl;
		String line[] = component.get(0).split(" ");
		String scname;

		int subcomponent = 0;
		int connections = 0;
		int pos;

		// System.out.println(" ");
		System.out.println("Processing a system implementation " + line[2]);
		String name[] = line[2].split("\\.");

		tempImpl = new AadlSystemImpl(line[2], name[0]);

		// Create or founded the main structure the additional structures of the
		// implementation are mapped.
		int size = component.size();
		for (String l : component) {
			l.trim();
			if (l.equalsIgnoreCase("subcomponents")) {
				subcomponent = 1;
				connections = 0;
				continue;
			}
			if (l.equalsIgnoreCase("connections")) {
				connections = 1;
				subcomponent = 0;
				continue;
			}
			if (--size == 0 || l.substring(0, 3).equalsIgnoreCase("end")) {
				connections = 0;
				subcomponent = 0;
				continue;
			}
			if (subcomponent == 1) {
				// Include all the system subcomponents on their declaration
				pos = 0;
				String subcomp[] = l.split(" ");
				if (!subcomp[1].equals(":"))
					pos = 1;
				else
					pos = 2;

				if (pos == 1)
					scname = subcomp[0].substring(0, subcomp[0].length() - 2);
				else
					scname = subcomp[0];

				AadlSubcomponent tempSubcomponent = new AadlSubcomponent(scname,
						subcomp[pos + 1].substring(0, subcomp[pos + 1].length() - 1).toLowerCase(),
						subcomp[pos].toLowerCase());
				// System.out.println(
				// subcomp[pos] + " " + subcomp[pos + 1].substring(0,
				// subcomp[pos + 1].length() - 1) + " added.");
				tempImpl.addSubcomponent(tempSubcomponent);
			}
			if (connections == 1) {
				// Include all the system subcomponents connections
				pos = 0;
				String connect[] = l.split(" ");
				if (!connect[1].equals(":")) {
					pos = 1;
					scname = connect[0].substring(0, connect[0].length() - 1);
				} else {
					pos = 2;
					scname = connect[0];
				}

				AadlConnection tempConnection = new AadlConnection(scname, connect[pos].toLowerCase(),
						connect[pos + 1].toLowerCase(), connect[pos + 3].substring(0, connect[pos + 3].length() - 1));
				// System.out.println("Connection: " + scname + " : " +
				// connect[pos] + " " + connect[pos + 1] + " --> "
				// + connect[pos + 3].substring(0, connect[pos + 3].length() -
				// 1));
				tempImpl.addConnection(tempConnection);
			}
		}

		aadlFile.addSystemImpl(tempImpl);
	}

	public void uppaalFileGeneration() {
		/*
		 * Generate the output file
		 */
		uppaalFile.generateFile(path + fileName);
	}

	/*
	 * Aux Functions
	 */

	private UppaalTemplate generateThreadTemplate(AadlThreadImpl thread) {
		UppaalTemplate tempTemplate = new UppaalTemplate();

		String name = thread.getName();

		// System.out.println(uppaalFile.getId());
		tempTemplate.setTemplateId(uppaalFile.getId());
		uppaalFile.incrementId();

		// System.out.println("Properties");

		for (AadlProperty p : thread.getProperties()) {
			if (p.getType().equalsIgnoreCase("priority")) {
				tempTemplate.setPriority(Integer.valueOf(p.getValue()));
				continue;
			}

			if (p.getType().equalsIgnoreCase("period")) {
				tempTemplate.setPeriod(Integer.valueOf(p.getValue()));
				continue;
			}

			if (p.getType().equalsIgnoreCase("deadline")) {
				tempTemplate.setDeadline(Integer.valueOf(p.getValue()));
				continue;
			}

			if (p.getType().equalsIgnoreCase("compute_execution_time")) {
				tempTemplate.setComputationTime(Integer.valueOf(p.getEndTime()));
				continue;
			}
		}

		// System.out.println("End");

		if (name.indexOf('.') > -1) {
			name = removeDotString(name);
		}

		tempTemplate.setName(name);

		// System.out.println("States");

		for (AadlState s : thread.getStates()) {
			UppaalLocation l = new UppaalLocation();
			l.setName(s.getName());
			l.setId("id" + tempTemplate.getId());

			if (s.isInitial())
				tempTemplate.setInit("id" + tempTemplate.getId());

			tempTemplate.incrementId();

			l.setX(Integer.toString(tempTemplate.getX()));
			l.setY(Integer.toString(tempTemplate.getY()));
			l.setNameX(Integer.toString(tempTemplate.getX()));
			l.setNameY(Integer.toString(tempTemplate.getY() + 20));

			tempTemplate.incrementX();
			tempTemplate.incrementY();

			tempTemplate.addLocation(l);
		}

		for (AadlVariable v : thread.getVariables()) {
			tempTemplate.addVariable(new UppaalVariable(v.getName(), v.getType(), ""));
		}

		AadlThread tempt = aadlFile.getThreadByName(thread.getParentName());

		// System.out.println("Variables Thread");

		for (AadlFeature f : tempt.getFeatures()) {
			if (!f.getDataType().isEmpty()) {
				if (uppaalFile.existsVariable(f.getName()) == -1) {
					if (f.getDataType().equalsIgnoreCase("chan")) {
						UppaalVariable temp = new UppaalVariable(f.getName(), f.getDataType(), "");
						temp.setBroadcast(true);
						uppaalFile.addVariable(temp);
					} else
						uppaalFile.addVariable(new UppaalVariable(f.getName(), f.getDataType(), ""));
				}
			}
		}

		// System.out.println("End");

		// System.out.println("Transitions");

		for (AadlTransition t : thread.getTransitions()) {
			String source = tempTemplate.getLocationId(t.getSource());
			String target = tempTemplate.getLocationId(t.getTarget());
			String update = "";
			String guard = "";
			String sync = "";

			// System.out.println(source + " - " + target);

			UppaalTransition tTransition = new UppaalTransition(source, target);

			if (!t.getGuard().isEmpty()) {
				guard += replaceEqualGuard(t.getGuard());
			}

			if (!t.getAction().isEmpty()) {
				// System.out.println(t.getAction());
				String act[] = t.getAction().split(";");
				int pos = 0;
				while (pos < act.length) {
					act[pos] = act[pos].trim();

					if (act[pos].indexOf(':') > -1) {
						/*
						 * Update
						 */
						if (update.isEmpty())
							update += act[pos].substring(0, act[pos].indexOf(':'))
									+ act[pos].substring(act[pos].indexOf(':') + 1);
						else
							update += ", " + act[pos].substring(0, act[pos].indexOf(':')) + " "
									+ act[pos].substring(act[pos].indexOf(':') + 1);
					}

					if (act[pos].substring(act[pos].length() - 1).equalsIgnoreCase("!")
							|| act[pos].substring(act[pos].length() - 1).equalsIgnoreCase("?")) {
						// System.out.println(act[pos]);
						// System.out.println(act[pos].substring(0,
						// act[pos].length() - 1));
						if (tempt.isSubprogramFeature(act[pos].substring(0, act[pos].length() - 1))) {
							// System.out.println("Subprogram");
							if (update.isEmpty())
								update += act[pos].substring(0, act[pos].length() - 1) + "()";
							else
								update += ", " + act[pos].substring(0, act[pos].length() - 1) + "()";
						} else
							sync += act[pos];
						pos++;
						continue;
					}

					if (act[pos].length() > 5) {
						if (act[pos].substring(0, 5).equalsIgnoreCase("guard")) {
							if (!guard.isEmpty()) {
								// TODO Evaluate the possibility of include the
								// connector on the subprogram
								guard += " and ";
							}
							while (act[pos].indexOf('"') > -1) {
								act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
								guard += act[pos].substring(0, act[pos].indexOf('"')) + " ";
								act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
							}
						}

						if (act[pos].indexOf('!') > -1) {
							if (tempt.isSubprogramFeature(act[pos].substring(0, act[pos].indexOf('!')))) {
								String locationName;
								switch (act[pos].substring(0, act[pos].indexOf('!')).toLowerCase()) {
								case "invariant":
									act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
									locationName = act[pos].substring(0, act[pos].indexOf('"'));
									act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
									act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
									String invariant = act[pos].substring(0, act[pos].indexOf('"'));
									tempTemplate.setLocationInvariant(locationName, invariant);
									break;

								case "exponentialrate":
									act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
									locationName = act[pos].substring(0, act[pos].indexOf('"'));
									act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
									act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
									String exponential = act[pos].substring(0, act[pos].indexOf('"'));
									tempTemplate.setLocationExponentialRate(locationName, exponential);
									break;
									
								case "init":
									act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
									locationName = act[pos].substring(0, act[pos].indexOf('"'));
									act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
									act[pos] = act[pos].substring(act[pos].indexOf('"') + 1);
									String init = act[pos].substring(0, act[pos].indexOf('"'));
									tempTemplate.setVariableInitValue(locationName, init);
									uppaalFile.setVariableInitValue(locationName, init);
									break;
									
								default:
									if (update.isEmpty())
										update += act[pos].substring(0, act[pos].indexOf('!'))
												+ act[pos].substring(act[pos].indexOf('!') + 1);
									else
										update += ", " + act[pos].substring(0, act[pos].indexOf('!'))
												+ act[pos].substring(act[pos].indexOf('!') + 1);
									break;
								}
								/*
								 * if (act[pos].substring(0,
								 * act[pos].indexOf('!')).equalsIgnoreCase("invariant")) { //
								 * System.out.println(act[pos]); act[pos] =
								 * act[pos].substring(act[pos].indexOf('"') + 1); //
								 * System.out.println(act[pos]); String locationName = act[pos].substring(0,
								 * act[pos].indexOf('"')); // System.out.println(locationName); act[pos] =
								 * act[pos].substring(act[pos].indexOf('"') + 1); //
								 * System.out.println(act[pos]); act[pos] =
								 * act[pos].substring(act[pos].indexOf('"') + 1); //
								 * System.out.println(act[pos]); String invariant = act[pos].substring(0,
								 * act[pos].indexOf('"')); // System.out.println(invariant); //
								 * System.out.println(locationName + " " + invariant);
								 * 
								 * tempTemplate.setLocationInvariant(locationName, invariant); } else { if
								 * (act[pos].substring(0, act[pos].indexOf('!'))
								 * .equalsIgnoreCase("exponentialrate")) { // System.out.println(act[pos]);
								 * act[pos] = act[pos].substring(act[pos].indexOf('"') + 1); //
								 * System.out.println(act[pos]); String locationName = act[pos].substring(0,
								 * act[pos].indexOf('"')); // System.out.println(locationName); act[pos] =
								 * act[pos].substring(act[pos].indexOf('"') + 1); //
								 * System.out.println(act[pos]); act[pos] =
								 * act[pos].substring(act[pos].indexOf('"') + 1); //
								 * System.out.println(act[pos]); String exponential = act[pos].substring(0,
								 * act[pos].indexOf('"')); // System.out.println(exponential);
								 * tempTemplate.setLocationExponentialRate(locationName, exponential); } else {
								 * 
								 * if (update.isEmpty()) update += act[pos].substring(0, act[pos].indexOf('!'))
								 * + act[pos].substring(act[pos].indexOf('!') + 1); else update += ", " +
								 * act[pos].substring(0, act[pos].indexOf('!')) +
								 * act[pos].substring(act[pos].indexOf('!') + 1); } }
								 */
							}
						}

					}

					// System.out.println(act[pos]);
					pos++;
				}
			}

			// System.out.println(guard + " --> " + update + " --> " + sync);

			if (!update.isEmpty()) {
				UppaalLabel up = new UppaalLabel(update, "assignment");
				tTransition.addLabel(up);
			}

			if (!guard.isEmpty()) {
				UppaalLabel lguard = new UppaalLabel(guard, "guard");
				tTransition.addLabel(lguard);
			}

			if (!sync.isEmpty()) {
				UppaalLabel lsync = new UppaalLabel(sync, "synchronisation");
				tTransition.addLabel(lsync);
			}

			tempTemplate.addTransition(tTransition);
		}

		return tempTemplate;
	}

	private String replaceEqualGuard(String input) {
		String guard = "";
		String auxGuard[] = input.split("=");

		int pos = 0;
		if (auxGuard.length > 0) {
			while (pos < auxGuard.length) {
				if (pos == auxGuard.length - 1)
					guard += auxGuard[pos];
				else {
					if (containOperator(auxGuard[pos]))
						guard += auxGuard[pos] + "=";
					else
						guard += auxGuard[pos] + "==";
				}
				pos++;
			}
		}

		if (guard.isEmpty())
			guard = input;

		return guard;
	}

	private boolean containOperator(String word) {
		/*
		 * Evaluate if the word contain one of this operators
		 */
		char validTags[] = { '<', '>', '!' };

		for (char tag : validTags) {
			if (word.indexOf(tag) > -1)
				return true;
		}
		return false;
	}

	private UppaalTemplate generateDeviceTemplate(AadlDeviceImpl device) {
		/*
		 * Perform the transformation of the imported devices on the timed automata
		 */
		String tempIdleId = "";
		String tempInitId = "";
		UppaalTemplate tempTemplate = new UppaalTemplate();

		if (device.getParentName().equalsIgnoreCase("scheduler")) {
			/*
			 * If a device is named as scheduler, its structure is ignored and the
			 * pre-implement scheduler is introduced
			 */
			tempTemplate.createScheduler();
			uppaalFile.insertSchedulerDeclarations();
			return tempTemplate;
		}

		if (device.getParentName().equalsIgnoreCase("behavior")) {
			/*
			 * If a device is named as scheduler, its structure is ignored and the
			 * pre-implement scheduler is introduced
			 */
			tempTemplate.createBehavior();
			uppaalFile.insertBehaviorDeclarations();
			return tempTemplate;
		}

		String name = device.getName();

		if (name.indexOf('.') > -1) {
			name = removeDotString(name);
		}

		// System.out.println(name);

		tempTemplate.setName(name);

		/*
		 * if (uppaalFile.existsVariable("run" + name) == -1) uppaalFile.addVariable(new
		 * UppaalVariable("run" + name, "chan", ""));
		 */

		/*
		 * if (uppaalFile.existsVariable(name + "Finish") == -1)
		 * uppaalFile.addVariable(new UppaalVariable(name + "Finish", "int", "0"));
		 */

		if (uppaalFile.existsVariable("errorOcr") == -1)
			uppaalFile.addVariable(new UppaalVariable("errorOcr", "int", "-1"));

		AadlErrorBehavior behavior;

		if (!device.getAnnexError().getBehavior().isEmpty()) {
			if (device.getAnnexError().getBehavior().indexOf(':') > -1) {
				behavior = aadlFile.getAnnexEmv2().getErrorBehaviorByName(device.getAnnexError().getBehavior()
						.substring(device.getAnnexError().getBehavior().indexOf(':') + 2));
			} else {
				behavior = aadlFile.getAnnexEmv2().getErrorBehaviorByName(device.getAnnexError().getBehavior());
			}

			for (AadlState s : behavior.getStates()) {
				UppaalLocation l = new UppaalLocation();
				l.setName(s.getName());
				l.setId("id" + tempTemplate.getId());
				// System.out.println("Name: " + l.getName() + " Id: " + l.getId());

				if (s.isInitial()) {
					l.addLabel(new UppaalLabel("1", "exponentialrate"));

					tempTemplate.setInit("id" + tempTemplate.getId());
					tempInitId = "id" + String.valueOf(tempTemplate.getId());
					tempTemplate.incrementId();

					/*
					 * If its initial add a branchpoint together with the location
					 */
					UppaalBranchpoint b = new UppaalBranchpoint("id" + tempTemplate.getId(), l.getId());
					b.setX(Integer.toString(tempTemplate.getX()));
					b.setY(Integer.toString(tempTemplate.getY()));

					tempTemplate.addBranchpoint(b);

					tempTemplate.incrementId();
					tempTemplate.incrementX();
					tempTemplate.incrementY();
				} else
					tempTemplate.incrementId();

				l.setX(Integer.toString(tempTemplate.getX()));
				l.setY(Integer.toString(tempTemplate.getY()));
				l.setNameX(Integer.toString(tempTemplate.getX()));
				l.setNameY(Integer.toString(tempTemplate.getY() + 20));

				tempTemplate.incrementX();
				tempTemplate.incrementY();

				if (!s.getName().equalsIgnoreCase("failed") && !s.isInitial()) {
					/*
					 * If its not final add a branchpoint together with the location
					 */
					UppaalBranchpoint b = new UppaalBranchpoint("id" + tempTemplate.getId(), l.getId());
					b.setX(Integer.toString(tempTemplate.getX()));
					b.setY(Integer.toString(tempTemplate.getY()));

					tempTemplate.addBranchpoint(b);

					tempTemplate.incrementId();
					tempTemplate.incrementX();
					tempTemplate.incrementY();
				}

				tempTemplate.addLocation(l);
			}

			UppaalLocation l = new UppaalLocation();

			if (!tempTemplate.searchLocationByName("idle")) {
				l.setName("idle");
			} else {
				l.setName("idleInitial");
			}

			l.setId("id" + tempTemplate.getId());
			tempIdleId = l.getId();

			l.setX(Integer.toString(tempTemplate.getX()));
			l.setY(Integer.toString(tempTemplate.getY()));
			l.setNameX(Integer.toString(tempTemplate.getX()));
			l.setNameY(Integer.toString(tempTemplate.getY() + 20));

			tempTemplate.incrementX();
			tempTemplate.incrementY();

			UppaalTransition begin = new UppaalTransition(l.getId(), tempTemplate.getInit());

			/*
			 * UppaalLabel lsync = new UppaalLabel("run" + name + "?", "synchronisation");
			 * begin.addLabel(lsync);
			 */

			/*
			 * lsync = new UppaalLabel(name + "Finish=0", "assignment");
			 * begin.addLabel(lsync);
			 */

			// tempTemplate.addTransition(new UppaalTransition(l.getId(),
			// tempTemplate.getInit()));
			tempTemplate.addTransition(begin);

			UppaalTransition endAct = new UppaalTransition(tempTemplate.getInit(), l.getId());

			/*
			 * UppaalLabel lendExec = new UppaalLabel(name + "Finish=1", "assignment");
			 * endAct.addLabel(lendExec);
			 */

			// tempTemplate.addTransition(new
			// UppaalTransition(tempTemplate.getInit(), l.getId()));
			tempTemplate.addTransition(endAct);

			tempTemplate.addLocation(l);

			tempTemplate.setInit(l.getId());

			/*
			 * for (AadlVariable v : device.get thread.getVariables()) {
			 * tempTemplate.addVariable(new UppaalVariable(v.getName(), v.getType(), "")); }
			 */

			// System.out.println("Transitions:" + behavior.getTransitions().size());

			for (AadlTransition t : behavior.getTransitions()) {
				String source = tempTemplate.getLocationId(t.getSource());
				String target = tempTemplate.getLocationId(t.getTarget());

				//System.out.println(source + " " + target + " " + tempInitId);

				UppaalTransition tTransition = new UppaalTransition(source, target);

				if (!t.getGuard().isEmpty()) {
					// System.out.println("G: " + t.getGuard());
					tTransition.addLabel(new UppaalLabel(t.getGuard(), "comments"));

					if (device.getAnnexError().getErrorPropertyOfError(t.getGuard()) != null) {
						AadlErrorProperty p = device.getAnnexError().getErrorPropertyOfError(t.getGuard());

						// System.out.println(p.getProperty() + " " +
						// p.getTarget() + " " + p.getProbValue());
						tTransition.addLabel(new UppaalLabel(p.getProbValue(), "probability"));
					}

				}

				if (!t.getAction().isEmpty()) {
					// System.out.println("A: " + t.getAction());
					tTransition.addLabel(new UppaalLabel(t.getAction(), "comments"));
				}

				tempTemplate.addTransition(tTransition);

				if (target.equalsIgnoreCase(tempInitId)) {
					//System.out.println("Here equals");
					UppaalTransition idleTransition = new UppaalTransition(source, tempIdleId);
					tempTemplate.addTransition(idleTransition);

					UppaalTransition idleTransition2 = new UppaalTransition(tempIdleId, source);
					tempTemplate.addTransition(idleTransition2);
				}

			}
		}

		return tempTemplate;
	}

	private String removeDotString(String word) {
		String aux;
		aux = word.substring(0, word.indexOf('.'));
		// aux += Character.toString(word.charAt(word.indexOf('.') +
		// 1)).toUpperCase();
		aux += word.substring(word.indexOf('.') + 1);
		return aux;
	}

	private AadlFeature generateFeature(String line) {
		/*
		 * Create a new aadl feature according the input
		 */

		String subprogramName = "";
		String directionName = ""; // IN, IN OUT, OUT, REQUIRES, PROVIDE
		String featureName = "";
		String typeName = ""; // PARAMETER, EVENT, EVENT DATA, DATA, SUBPROGRAM
								// ACCESS
		String dataType = "";

		// Include all the subprogram subcomponents
		int type = -1;
		int pos = -1;

		String subcomp[] = line.split(" ");
		// System.out.println(line);

		if (!subcomp[1].equals(":")) {
			pos = 1;
			featureName = subcomp[0].substring(0, subcomp[0].length() - 1);
		} else {
			pos = 2;
			featureName = subcomp[0];
		}

		if (subcomp[pos].equalsIgnoreCase("in") || subcomp[pos].equalsIgnoreCase("out")) {
			directionName = subcomp[pos];
			type = 0;
			pos++;
			if (subcomp[pos].equalsIgnoreCase("out")) {
				directionName += " " + subcomp[pos];
				pos++;
			}
		} else {
			type = 1;
			directionName = subcomp[pos];
			pos++;
		}

		if (type == 0) {
			if (subcomp[pos].substring(subcomp[pos].length() - 1).equalsIgnoreCase(";"))
				typeName = subcomp[pos].substring(0, subcomp[pos].length() - 1);
			else
				typeName = subcomp[pos];

			if (subcomp[pos].equalsIgnoreCase("event")) {
				if (subcomp[pos + 1].equalsIgnoreCase("data")) {
					typeName += " " + subcomp[pos + 1];
				}
			}

			if (subcomp[subcomp.length - 1].length() > 1) {
				if (!subcomp[subcomp.length - 1].equalsIgnoreCase("port;")
						&& !subcomp[subcomp.length - 1].equalsIgnoreCase("parameter;"))
					dataType = subcomp[subcomp.length - 1].substring(0, subcomp[subcomp.length - 1].length() - 1);
			} else {
				if (!subcomp[subcomp.length - 2].equalsIgnoreCase("port;")
						&& !subcomp[subcomp.length - 2].equalsIgnoreCase("parameter;"))
					dataType = subcomp[subcomp.length - 2];
			}
		} else if (type == 1) {
			typeName = subcomp[pos] + " " + subcomp[pos + 1];
			subprogramName = subcomp[pos + 2].substring(0, subcomp[pos + 2].length() - 1);
		}

		// System.out.println(featureName + " : " + directionName + " " +
		// typeName + " " + subprogramName + " " + dataType);
		return new AadlFeature(featureName, directionName, typeName, subprogramName, dataType);
		// return tempFeature;
	}

	private AadlProperty generateProperty(String line) {
		/*
		 * Create a new aadl property according the input
		 */
		String propertyName = "";
		String measurement;
		String type;

		int initTime;
		int endTime;
		int pos = -1;

		String property[] = line.split(" ");
		if (!property[1].equals("=>")) {
			pos = 1;
			propertyName = property[0].substring(0, property[0].length() - 2).toLowerCase();
		} else {
			pos = 2;
			propertyName = property[0].toLowerCase();
		}

		if (propertyName.equalsIgnoreCase("compute_execution_time")) {
			initTime = Integer.valueOf(property[pos]);
			measurement = property[pos + 1];
			endTime = Integer.valueOf(property[pos + 3]);
			return new AadlProperty(propertyName, initTime, endTime, measurement);
			// System.out.println(propertyName + " " + initTime + " " + endTime
			// + " " + measurement + " added.");
		}

		if (propertyName.equalsIgnoreCase("priority")) {
			type = property[pos].substring(0, property[pos].length() - 1);
			return new AadlProperty(propertyName, type);
			// System.out.println(propertyName + " " + type + " added.");
		}

		if (propertyName.equalsIgnoreCase("dispatch_protocol")) {
			type = property[pos].substring(0, property[pos].length() - 1);
			return new AadlProperty(propertyName, type);
			// System.out.println(propertyName + " " + type + " added.");
		}

		if (propertyName.equalsIgnoreCase("period") || propertyName.equalsIgnoreCase("deadline")) {
			type = property[pos];
			measurement = property[pos + 1].substring(0, property[pos + 1].length() - 1);
			return new AadlProperty(propertyName, type, measurement);
			// System.out.println(propertyName + " " + type + " " + measurement
			// + " added.");
		}

		if (propertyName.equalsIgnoreCase("source_language")) {
			type = property[pos].substring(1, property[pos].length() - 2);
			return new AadlProperty(propertyName, type);
			// System.out.println(propertyName + " " + type + " added.");
		}

		if (propertyName.equalsIgnoreCase("source_text")) {
			type = property[pos].substring(2, property[pos].length() - 3);
			return new AadlProperty(propertyName, type);
			// System.out.println(propertyName + " " + type + " added.");
		}

		return null;
	}

	private boolean isFinalAction(String word) {
		/*
		 * Evaluate if is the last action of the block
		 */
		if (word.length() >= 2)
			return word.substring(word.length() - 2).equalsIgnoreCase("};");
		else
			return false;
	}

	private int containNextCondition(String word) {
		/*
		 * Evaluate if the sentece contains =>
		 */
		if (word.length() >= 2) {
			for (int i = 0; i < word.length(); i++) {
				if (Character.toString(word.charAt(i)).equals("="))
					if (Character.toString(word.charAt(i + 1)).equals(">"))
						return i;
			}
		}
		return -1;
	}

	private boolean ignoreLine(String line) {
		/*
		 * Evaluate if the line have one of the tags that are ignored
		 */
		String ignoreTags[] = { "package", "public", "with" };
		String lineParts[];
		lineParts = line.split(" ");
		for (String s : lineParts) {
			for (String tag : ignoreTags) {
				if (s.equalsIgnoreCase(tag))
					return true;
			}
		}
		return false;
	}

	private String getComponentName(String line) {
		String aux[] = line.split(" ");

		if (aux[1].equalsIgnoreCase("implementation")) {
			// System.out.println("Name: " + aux[2]);
			return aux[2];
		} else {
			// System.out.println("Name: " + aux[1]);
			return aux[1];
		}
	}

	private boolean endComponent(String line, String name) {
		/*
		 * Evaluate if is the end of a component
		 */
		String aux[] = line.split(" ");
		// System.out.println(line + " - " + name);
		if (name.equalsIgnoreCase("emv2")) {
			// System.out.println(aux[0] + " " +
			// aux[0].equalsIgnoreCase("**};"));
			return aux[0].equalsIgnoreCase("**};");
		} else {
			if (aux.length > 1) {
				if (aux[0].equalsIgnoreCase("end") && aux[1].substring(0, aux[1].length() - 1).equalsIgnoreCase(name)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean componentInitialTag(String line) {
		/*
		 * At this time just system, process, device thread
		 */
		String validTags[] = { SYSTEM, PROCESS, DEVICE, THREAD, DATA, SUBPROGRAM, ANNEX };

		String lineParts[] = line.split(" ");

		for (String s : lineParts) {
			for (String tag : validTags) {
				if (s.equalsIgnoreCase(tag)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isErrorAnnex(String word) {
		/*
		 * Evaluate if is as error annex
		 */
		String line[] = word.split(" ");
		if (line.length >= 2) {
			if (line[0].equalsIgnoreCase("annex")) {
				if (line[1].substring(0, 4).equalsIgnoreCase("emv2"))
					return true;
			}
		}
		return false;
	}

	private AadlErrorProperty generateErrorProperty(String word) {
		/*
		 * Generate the error annex properties
		 */
		int distribution = 0;
		int open = -1;
		int end = -1;
		int pos = 0;

		String property = "";
		String target = "";

		String errorLine[] = word.split(" ");

		while (pos < errorLine.length) {
			if (pos == 0) {
				if (containNextCondition(errorLine[pos]) == -1) {
					if (errorLine[pos].equalsIgnoreCase("EMV2::OccurrenceDistribution")) {
						distribution = 1;
					}
				} else {
					open = containNextCondition(errorLine[pos]);
					if (errorLine[pos].substring(0, open).equalsIgnoreCase("EMV2::OccurrenceDistribution")) {
						distribution = 1;
						open = -1;
					}
				}
			}

			if (distribution == 1) {
				if (open == -1) {
					if (getCharPositionInString(errorLine[pos], "[") > -1) {
						// if (containBeginOfDistribution(errorLine[pos]) > -1)
						// {
						open = getCharPositionInString(errorLine[pos], "[");
						// open = containBeginOfDistribution(errorLine[pos]);
						if (getCharPositionInString(errorLine[pos], "]") > -1) {
							// if (containEndOfDistribution(errorLine[pos]) >
							// -1) {
							// end = containEndOfDistribution(errorLine[pos]);
							end = getCharPositionInString(errorLine[pos], "]");
							property = errorLine[pos].substring(open + 1, end);
						} else {
							property = errorLine[pos].substring(open + 1);
						}
					}
				} else {
					if (end == -1) {
						if (getCharPositionInString(errorLine[pos], "]") > -1) {
							// if (containEndOfDistribution(errorLine[pos]) >
							// -1) {
							// end = containEndOfDistribution(errorLine[pos]);
							end = getCharPositionInString(errorLine[pos], "]");
							property += " " + errorLine[pos].substring(0, end);
						} else {
							property += " " + errorLine[pos];
						}
					}
				}

				if (end > -1) {
					if (pos == errorLine.length - 1) {
						target = errorLine[pos].substring(0, errorLine[pos].length() - 1);
					}
				}
			}
			pos++;
		}

		property = property.trim();

		// System.out.println("Property: " + property + " Target: " + target);

		return new AadlErrorProperty(property, target);
	}

	private int getCharPositionInString(String word, String c) {
		/*
		 * Evaluate if contain the input character and return its position
		 */
		for (int i = 0; i < word.length(); i++) {
			if (Character.toString(word.charAt(i)).equalsIgnoreCase(c))
				return i;
		}
		return -1;
	}

	private AadlTransition generateTransition(String l, boolean getName) {
		/*
		 * Generate system transitions
		 * review parser when do you have more then 1 space 
		 */

		String tSource = "";
		String tTarget = "";
		String tUpdate = "";
		String tGuard = "";
		String tName = "";
		int openAction = -1;
		int open = -1;
		int end = -1;
		int pos = 0;

		String behavior[] = l.split(" ");

		//System.out.println(l);

		if (getName) {

			if (getCharPositionInString(behavior[pos], ":") > -1) {
				open = getCharPositionInString(behavior[pos], ":");
				tName = behavior[pos].substring(0, open);
				if (open < behavior[pos].length() - 1) {
					behavior[pos] = behavior[pos].substring(open + 1);
				} else {
					pos++;
				}
			} else {
				tName = "";
				while (getCharPositionInString(behavior[pos + 1], ":") == -1) {
					tName += behavior[pos];
					pos++;
				}
				if (getCharPositionInString(behavior[pos + 1], ":") > -1) {
					tName += behavior[pos];
					pos++;
					if (behavior[pos].length() > 1) {
						open = getCharPositionInString(behavior[pos], ":");
						behavior[pos] = behavior[pos].substring(open + 1);
					} else {
						pos++;
					}
				}
			}
			open = -1;
		}

		if (getBeginOfCondition(behavior[pos]) > -1) {
			/*
			 * Guard opened
			 */
			// System.out.println("Begin condition coupled with source name");
			open = getBeginOfCondition(behavior[pos]);
			tSource = behavior[pos].substring(0, open - 1);
			if (getEndOfCondition(behavior[pos]) > -1) {
				/*
				 * Guard ended
				 */
				// System.out.println("End condition coupled with source name");
				end = getEndOfCondition(behavior[pos]);
				tGuard = behavior[pos].substring(open + 1, end - 2);
				if (getCharPositionInString(behavior[pos], "{") > -1) {
					// if (containBeginningOfActionBlock(behavior[pos]) > -1) {
					/*
					 * Action block opened
					 */

					openAction = getCharPositionInString(behavior[pos], "{");
					// openAction =
					// containBeginningOfActionBlock(behavior[pos]);
					tTarget = behavior[pos].substring(end + 1, openAction);
					if (isFinalAction(behavior[pos])) {
						tUpdate = behavior[pos].substring(openAction + 1, behavior[pos].length() - 2);
					} else {
						tUpdate = behavior[pos].substring(openAction + 1);
					}
				} else {
					tTarget = behavior[pos].substring(end + 1, behavior[pos].length() - 1);
				}
			} else {
				// System.out.println("End condition not coupled with source
				// name");
				tGuard = behavior[pos].substring(open + 1);
			}
		} else {
			tSource = behavior[pos];
			// System.out.println("Source: " + source);
		}

		pos++;

		if (open == -1) {
			/*
			 * Guard not opened
			 */
			// System.out.println("Not opened");
			if (getBeginOfCondition(behavior[pos]) > -1) {
				/*
				 * Guard opened
				 */
				// System.out.println("Guard open");
				open = getBeginOfCondition(behavior[pos]);
				if (getEndOfCondition(behavior[pos]) > -1) {
					/*
					 * Guard opened and ended
					 */
					// System.out.println("End of Guard");
					end = getEndOfCondition(behavior[pos]);
					if (getCharPositionInString(behavior[pos], "{") > -1) {
						// if (containBeginningOfActionBlock(behavior[pos]) >
						// -1) {
						/*
						 * Update coupled with the guard
						 */
						tGuard = behavior[pos].substring(open + 1, end - 2);
						openAction = getCharPositionInString(behavior[pos], "{");
						// openAction =
						// containBeginningOfActionBlock(behavior[pos]);
						tTarget = behavior[pos].substring(end + 1, openAction);
						if (isFinalAction(behavior[pos])) {
							tUpdate = behavior[pos].substring(openAction + 1, behavior[pos].length() - 2);
						} else {
							tUpdate = behavior[pos].substring(openAction + 1);
						}
					} else {
						tGuard = behavior[pos].substring(open + 1, end - 2);
						if (behavior[pos].length() > end + 1) {
							/*
							 * Guard coupled with the target
							 */
							// System.out.println("Guard not coupled with the
							// target");
							tTarget = behavior[pos].substring(end + 1, behavior[pos].length() - 1);
						}
					}
				} else {
					/*
					 * Guard not ended
					 */
					// System.out.println("Guard not ended");
					if (behavior[pos].length() >= 3) {
						/*
						 * Open Guard coupled with expression
						 */
						tGuard = behavior[pos].substring(open + 1);
					}
				}
			}
			pos++;
		}

		if (behavior.length > 1) {
			// Include update verification
			// pos = 2;
			while (pos < behavior.length) {
				if (getEndOfCondition(behavior[pos]) > -1) {
					/*
					 * Guard opened and ended
					 */
					// System.out.println("End of guard");
					end = getEndOfCondition(behavior[pos]);
					tGuard += " " + behavior[pos].substring(0, end - 2);
					if (behavior[pos].length() > end + 1) {
						/*
						 * Guard coupled with the target
						 */
						// System.out.println("Guard coupled with the target");
						if (getCharPositionInString(behavior[pos], "{") > -1) {
							// if (containBeginningOfActionBlock(behavior[pos])
							// > -1) {
							openAction = getCharPositionInString(behavior[pos], "{");
							// openAction =
							// containBeginningOfActionBlock(behavior[pos]);
							tTarget = behavior[pos].substring(end + 1, openAction - 1);
							// tTarget = behavior[pos].substring(end + 1,
							// containBeginningOfActionBlock(behavior[pos]) -
							// 1);
							if (isFinalAction(behavior[pos])) {
								tUpdate += " " + behavior[pos].substring(openAction + 1, behavior[pos].length() - 2);
							} else {
								tUpdate += " " + behavior[pos].substring(openAction + 1);
							}
						} else {
							if (getCharPositionInString(behavior[pos], ";") > -1) {
								// if (isActionClosed(behavior[pos])) {
								tTarget = behavior[pos].substring(end + 1, behavior[pos].length() - 1);
							} else {
								tTarget = behavior[pos].substring(end + 1);
							}
						}
					}
				} else {
					if (end > -1) {
						// System.out.println("Get target");
						if (openAction > -1) {
							if (isFinalAction(behavior[pos])) {
								tUpdate += " " + behavior[pos].substring(0, behavior[pos].length() - 2);
							} else {
								tUpdate += " " + behavior[pos];
							}
						} else {
							if (getCharPositionInString(behavior[pos], "{") > -1) {
								// if
								// (containBeginningOfActionBlock(behavior[pos])
								// > -1) {
								openAction = getCharPositionInString(behavior[pos], "{");
								// openAction =
								// containBeginningOfActionBlock(behavior[pos]);

								if (tTarget.isEmpty())
									tTarget = behavior[pos].substring(0, openAction);

								if (isFinalAction(behavior[pos])) {
									tUpdate = behavior[pos].substring(openAction + 1, behavior[pos].length() - 2);
								} else {
									tUpdate = behavior[pos].substring(openAction + 1);
								}
							} else {
								if (getCharPositionInString(behavior[pos], ";") > -1) {
									// if (isActionClosed(behavior[pos])) {
									tTarget = behavior[pos].substring(0, behavior[pos].length() - 1);
								} else {
									tTarget = behavior[pos];
								}
							}
						}
					} else {
						tGuard += " " + behavior[pos];
					}
				}
				pos++;
			}
		}

		tGuard = tGuard.trim();

		System.out.println("Name: " + tName + " Source: " + tSource + " Guard: " + tGuard + " Target: " + tTarget + " Update: " + tUpdate);

		return new AadlTransition(tName, tSource, tGuard, tTarget, tUpdate);
	}

	private int getBeginOfCondition(String word) {
		/*
		 * Evaluate if a condition of a transition is ended
		 */
		int init = -1;

		if (word.length() >= 2) {
			for (int i = 0; i < word.length(); i++) {
				if (Character.toString(word.charAt(i)).equals("-") || init == 1) {
					init = 1;
					if (Character.toString(word.charAt(i)).equals("["))
						return i;
				}
			}
		}
		return -1;
	}

	private int getEndOfCondition(String word) {
		/*
		 * Evaluate if a condition of a transition is ended
		 */
		int init = -1;
		int second = -1;

		if (word.length() >= 3) {
			for (int i = 0; i < word.length(); i++) {
				if (Character.toString(word.charAt(i)).equals("]") || init == 1) {
					init = 1;
					if (Character.toString(word.charAt(i)).equals("-") || second == 1) {
						second = 1;
						if (Character.toString(word.charAt(i)).equals(">")) {
							return i;
						}
					}
				}
			}
		}
		return -1;
	}

	private AadlErrorPropagation generateErrorPropagation(String line) {
		/*
		 * Generate a error annex propagation
		 */
		String propagation = "";
		String direction = "";
		String name = "";

		int pos = -1;
		int open = -1;
		int ended = -1;

		String word[] = line.split(" ");

		if (word.length > 2) {

			if (word[1].equalsIgnoreCase(":")) {
				name = word[0];
				pos = 1;
			} else {
				name = word[0].substring(0, word[0].length() - 1);
				pos = 0;
			}

			pos++;
			direction = word[pos];
			pos++;

			// System.out.println(pos);

			while (pos < word.length) {
				if (getCharPositionInString(word[pos], "{") > -1) {
					// if (containBeginningOfActionBlock(word[pos]) > -1) {
					open = getCharPositionInString(word[pos], "{");
					// open = containBeginningOfActionBlock(word[pos]);
					if (isFinalAction(word[pos])) {
						propagation = word[pos].substring(1, word[pos].length() - 2);
						ended = 1;
					} else
						propagation = word[pos].substring(open + 1);
				} else {
					if (open > -1) {
						if (isFinalAction(word[pos])) {
							ended = 1;
							propagation += word[pos].substring(0, word[pos].length() - 2);
						} else
							propagation += word[pos];
					}
				}
				pos++;
			}

			if (ended == -1) {
				if (isFinalAction(word[pos]))
					propagation += word[pos].substring(0, word[pos].length() - 2);
			}

		} else {
			pos++;
			if (getCharPositionInString(word[pos], ":") > -1) {
				// if (getEndOfNamePosition(word[pos]) > -1) {
				open = getCharPositionInString(word[pos], ":");
				// open = getEndOfNamePosition(word[pos]);
				name = word[pos].substring(0, open);
				direction = word[pos].substring(open + 1);
			}

			pos++;

			open = getCharPositionInString(word[pos], "{");
			// open = containBeginningOfActionBlock(word[pos]);

			propagation = word[pos].substring(open + 1, word[pos].length() - 2);
		}

		// System.out.println("Name: " + name + " Direction: " + direction + "
		// Propagation: " + propagation);

		return new AadlErrorPropagation(name, direction, propagation);
	}
}
