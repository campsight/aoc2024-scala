import networkx as nx


def parse_input(input_file):
    """
    Parses the input file into initial wire values and gate definitions.
    """
    wire_values = {}
    gates = []

    with open(input_file, "r") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue

            if ":" in line:  # Initial wire values
                wire, value = line.split(":")
                wire_values[wire.strip()] = int(value.strip())
            elif "->" in line:  # Gate definitions
                parts = line.split("->")
                left = parts[0].strip()
                output = parts[1].strip()

                if "AND" in left:
                    in1, in2 = map(str.strip, left.split("AND"))
                    gates.append((in1, "AND", in2, output))
                elif "XOR" in left:
                    in1, in2 = map(str.strip, left.split("XOR"))
                    gates.append((in1, "XOR", in2, output))
                elif "OR" in left:
                    in1, in2 = map(str.strip, left.split("OR"))
                    gates.append((in1, "OR", in2, output))
                else:
                    raise ValueError(f"Unknown gate format: {line}")

    return wire_values, gates


def evaluate_gate(op, val1, val2):
    """
    Evaluates a boolean operation (AND, OR, XOR) on two values.
    """
    if op == "AND":
        return val1 & val2
    elif op == "OR":
        return val1 | val2
    elif op == "XOR":
        return val1 ^ val2
    else:
        raise ValueError(f"Unknown operation: {op}")

def simulate_circuit(input_file):
    """
    Simulates the circuit described in the input file.
    """
    # Parse input
    wire_values, gates = parse_input(input_file)

    # Build dependency graph
    G = nx.DiGraph()

    for in1, op, in2, out in gates:
        G.add_edge(in1, out)
        G.add_edge(in2, out)

    # Perform topological sorting
    topo_order = list(nx.topological_sort(G))

    # Simulate the circuit
    for wire in topo_order:
        if wire in wire_values:
            continue  # Skip wires with predefined values

        # Find gates that produce this wire's value
        for in1, op, in2, out in gates:
            if out == wire:
                # Evaluate the gate
                val1 = wire_values[in1]
                val2 = wire_values[in2]
                wire_values[out] = evaluate_gate(op, val1, val2)

    # Extract and combine `z` wires
    z_wires = {k: v for k, v in wire_values.items() if k.startswith("z")}
    sorted_z_wires = sorted(z_wires.items(), key=lambda x: int(x[0][1:]))

    # Convert to binary and then decimal
    binary_result = "".join(str(v) for _, v in reversed(sorted_z_wires))
    decimal_result = int(binary_result, 2)

    return decimal_result, (wire_values, gates, G)

if __name__ == "__main__":
    input_file = "2024_day24_input.txt"

    decimal, _ = simulate_circuit(input_file)
    print(decimal)
