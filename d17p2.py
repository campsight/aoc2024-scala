def run(A):
    """ Simulate the program and return its output. """
    B, C = 0, 0
    output = []

    while A > 0:
        B = A % 8  # 2,4: B = A % 8
        B ^= 1     # 1,1: B = B ^ 1
        C = A // (1 << B)  # 7,5: C = A // (2^B)
        B ^= 5     # 1,5: B = B ^ 5
        B ^= C     # 4,3: B = B ^ C
        output.append(B % 8)  # 5,5: Output B % 8
        A //= 8    # 0,3: A = A // 8
    return output

def recombine(sequence):
    """ Recombine the 3-bit sequences into the full value of A. """
    result = sequence[0]
    shift = 10
    for value in sequence[1:]:
        result += (value >> 7) << shift
        shift += 3
    return result

def find_minimal_A(program):
    """
    Find the minimal starting value of A that reproduces the program output.
    """
    def safe_run(a):
        """ Wrap run(a) to handle empty outputs gracefully. """
        output = run(a)
        return output[0] if output else -1  # Use -1 as a placeholder for no output

    # Step 1: Precompute all 10-bit candidates
    steps = [safe_run(a) for a in range(2 ** 10)]

    # Step 2: Start with candidates that match the first program output
    valid_sequences = [[i] for i in range(2 ** 10) if steps[i] == program[0]]

    # Step 3: Build valid sequences for each subsequent program output
    for target in program[1:]:
        next_sequences = []
        for seq in valid_sequences:
            current = seq[-1] >> 3  # Shift by 3 bits
            for i in range(8):  # Test all possible lower 3 bits
                candidate = (i << 7) + current
                if steps[candidate] == target:
                    next_sequences.append(seq + [candidate])
        valid_sequences = next_sequences

    # Step 4: Recombine sequences and validate full program
    min_A = float('inf')
    print(valid_sequences)
    for seq in valid_sequences:
        A = recombine(seq)
        if run(A) == program:
            min_A = min(min_A, A)

    return min_A


# Input program
program = [2, 4, 1, 1, 7, 5, 1, 5, 4, 1, 5, 5, 0, 3, 3, 0]

# Find and print the minimal A
minimal_A = find_minimal_A(program)
print("The minimal A that reproduces the program is:", minimal_A)
