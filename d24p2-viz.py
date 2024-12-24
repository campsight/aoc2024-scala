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
                    gates.append({"a": in1, "op": "AND", "b": in2, "output": output})
                elif "XOR" in left:
                    in1, in2 = map(str.strip, left.split("XOR"))
                    gates.append({"a": in1, "op": "XOR", "b": in2, "output": output})
                elif "OR" in left:
                    in1, in2 = map(str.strip, left.split("OR"))
                    gates.append({"a": in1, "op": "OR", "b": in2, "output": output})
                else:
                    raise ValueError(f"Unknown gate format: {line}")

    return wire_values, gates

def find_gate(gates, a, b, op):
    """
    Finds a gate matching the given inputs and operation, accounting for input order.
    """
    return next((g for g in gates if (g["a"] == a and g["b"] == b or g["a"] == b and g["b"] == a) and g["op"] == op), {"output": "!!!"})

def find_gate_part(gates, in1, op):
    """
    Finds a gate matching the given input and operation.
    """
    return next((g for g in gates if (g["a"] == in1 or g["b"] == in1) and g["op"] == op), {"output": "!!!"})

def visualize_circuit_full_adder(wire_values, gates, swaps):
    """
    Creates an ASCII visualization of the full adder circuit for all bits.
    """
    visualization = []
    input_bits = sorted([wire for wire in wire_values if wire.startswith("x")], key=lambda w: int(w[1:]))
    num_bits = len(input_bits)

    # Initialize carrier bit
    carrier_bit = find_gate(gates, "x00", "y00", "AND")["output"]

    # Apply swaps to the gates
    swap_dict = {swaps[i]: swaps[i + 1] for i in range(0, len(swaps), 2)}
    swap_dict.update({swaps[i + 1]: swaps[i] for i in range(0, len(swaps), 2)})

    for gate in gates:
        if gate["output"] in swap_dict:
            gate["output"] = swap_dict[gate["output"]]

    for i in range(1, num_bits):
        x = f"x{i:02d}"
        y = f"y{i:02d}"
        z = f"z{i:02d}"

        # Initialize variables for debugging
        xor1 = {"output": "uninitialized"}
        and1 = {"output": "uninitialized"}
        xor2 = {"output": "uninitialized"}
        and2 = {"output": "uninitialized"}
        or_gate = {"output": "uninitialized"}

        try:
            # Find gates corresponding to this bit
            xor1 = find_gate(gates, x, y, "XOR")
            and1 = find_gate(gates, x, y, "AND")
            xor2 = find_gate(gates, xor1["output"], carrier_bit, "XOR")
            or_gate = find_gate_part(gates, and1["output"], "OR")
            and2 = find_gate(gates, xor1["output"], carrier_bit, "AND")


            # Update carrier bit for the next iteration
            next_carrier_bit = or_gate["output"]

            xor2_in1 = xor2['a'] if xor1["output"] == xor2['a'] else xor2['b']
            xor2_in2 = xor2['b'] if xor1["output"] == xor2['a'] else xor2['a']

            # Build visualization for this bit
            visualization.append(f"{i:02d}================={i:02d}====================={i:02d}")
            visualization.append(f"{x} --|")
            visualization.append(f"      | [XOR] --> {xor2_in1} --|")
            visualization.append(f"{y} --|                 | [XOR] --> {z}")
            visualization.append(f"                  {xor2_in2} --| ")
            visualization.append("")
            visualization.append(f"{x} --|")
            visualization.append(f"      | [AND] --> {and1['output']} --| ")
            visualization.append(f"{y} --|                 | ")
            visualization.append(f"                        | [OR] --> {next_carrier_bit}")
            visualization.append(f"{xor1['output']} --|                 |")
            visualization.append(f"      | [AND] --> {and2['output']} --|")
            visualization.append(f"{carrier_bit} --|")

            carrier_bit = next_carrier_bit
            if carrier_bit == "!!!":
                raise Exception("Carry bit error")

        except Exception as e:
            visualization.append(f"Error processing bit {i}: {e}")
            visualization.append(f"xor1: {xor1}")
            visualization.append(f"xor2: {xor2}")
            visualization.append(f"and1: {and1}")
            visualization.append(f"and2: {and2}")
            visualization.append(f"or: {or_gate}")
            visualization.append("")

    print("\n".join(visualization))

def process_circuit(input_file):
    """
    Processes the circuit and visualizes the full adder for all bits.
    """
    wire_values, gates = parse_input(input_file)
    # initialize => see what is wrong and correct one by one
    swaps = ["z00", "z00", "z00", "z00", "z00", "z00", "z00", "z00"]
    # my solution swaps = ["z06", "ksv", "kbs", "nbd", "z20", "tqq", "z39", "ckb"]
    # Visualize the circuit
    print("Circuit Visualization:")
    visualize_circuit_full_adder(wire_values, gates, swaps)
    return swaps

if __name__ == "__main__":
    input_file = "2024_day24_input.txt"
    swaps = process_circuit(input_file)
    print(",".join(sorted(swaps)))
