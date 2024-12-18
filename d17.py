def execute_program(registers, program):
    # Registers
    A, B, C = registers['A'], registers['B'], registers['C']

    # Helper function to evaluate combo operands
    def combo_value(operand):
        if operand <= 3:
            return operand
        elif operand == 4:
            return A
        elif operand == 5:
            return B
        elif operand == 6:
            return C
        else:
            raise ValueError("Invalid combo operand 7 encountered.")

    # Instruction pointer and output buffer
    ip = 0  # Instruction pointer starts at 0
    output = []

    # Program execution loop
    while ip < len(program):
        opcode = program[ip]
        operand = program[ip + 1] if ip + 1 < len(program) else 0  # Ensure valid operand access

        # Handle instructions
        if opcode == 0:  # adv: A = A // (2 ** combo_value(operand))
            A //= (2 ** combo_value(operand))
        elif opcode == 1:  # bxl: B = B ^ operand
            B ^= operand
        elif opcode == 2:  # bst: B = combo_value(operand) % 8
            B = combo_value(operand) % 8
        elif opcode == 3:  # jnz: if A != 0, jump to operand
            if A != 0:
                ip = operand
                continue  # Skip ip increment after a jump
        elif opcode == 4:  # bxc: B = B ^ C
            B ^= C
        elif opcode == 5:  # out: output combo_value(operand) % 8
            output.append(combo_value(operand) % 8)
        elif opcode == 6:  # bdv: B = A // (2 ** combo_value(operand))
            B = A // (2 ** combo_value(operand))
        elif opcode == 7:  # cdv: C = A // (2 ** combo_value(operand))
            C = A // (2 ** combo_value(operand))
        else:
            raise ValueError(f"Unknown opcode: {opcode}")

        print(f"ip = {ip}, A = {A}, B = {B}, C= {C}, opcode = {opcode}, operand = {operand}, output = {output}")

        # Move to the next instruction
        ip += 2

    return ",".join(map(str, output))


# Input file handling
def main():
    # Initialize registers
    # registers = {'A': 729, 'B': 0, 'C': 0}
    registers = {'A': 17323786, 'B': 0, 'C': 0}

    # Read input program from file
    input_file = "2024_day17_input.txt"  # Replace with your actual input file path
    with open(input_file, 'r') as file:
        program = list(map(int, file.read().split(",")))  # Convert file contents to a list of integers

    # Execute the program and get the output
    result = execute_program(registers, program)

    # Print the final output
    print("Output:", result)


if __name__ == "__main__":
    main()
