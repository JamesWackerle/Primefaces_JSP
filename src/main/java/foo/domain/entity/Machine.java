package foo.domain.entity;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;

public class Machine {

	private String machine;
	private String machineDescription;

	// Hash map that pairs a Machine code to a readable Machine Description
	public static final HashMap<String, String> MACHINE_TO_DESC = new HashMap<String, String>();

	private static Connection con;
	private static Statement smt;

	public Machine(String machine) {
		super();
		this.machine = machine;

		if (MACHINE_TO_DESC.get(machine) != null) {
			this.machineDescription = MACHINE_TO_DESC.get(machine);
		} else {
			this.machineDescription = "Machine Description not found";
		}

	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getMachineDescription() {
		return machineDescription;
	}

	public void setMachineDescription(String machineDescription) {
		this.machineDescription = machineDescription;
	}

	public static HashMap<String, String> getMachineToDesc() {
		return MACHINE_TO_DESC;
	}

	@Override
	public String toString() {
		return machine;
	}

}
