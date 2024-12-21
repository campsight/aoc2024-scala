import sys
from functools import cache
from itertools import permutations


def parse_input(input_file_name) -> list[str]:
    """
    Read puzzle instructions from the provided file.
    """
    with open(input_file_name, 'r') as f:
        lines = f.read().splitlines()
    return lines


# Layout of the numeric keypad (omitting spaces), mapped to 2D positions
NUMERIC_LAYOUT = [
    '789',
    '456',
    '123',
    ' 0A'
]
NUM_KEYPAD = {
    char: (x, y)
    for y, row in enumerate(NUMERIC_LAYOUT)
    for x, char in enumerate(row)
    if char != ' '
}

# Layout of the directional keypad (omitting spaces), mapped to 2D positions
DIRECTIONAL_LAYOUT = [
    ' ^A',
    '<v>'
]
DIR_KEYPAD = {
    char: (x, y)
    for y, row in enumerate(DIRECTIONAL_LAYOUT)
    for x, char in enumerate(row)
    if char != ' '
}

# Movement vectors for each direction character
MOVES = {
    '^': (0, -1),
    '>': (1, 0),
    'v': (0, 1),
    '<': (-1, 0)
}


@cache
def compute_keypresses(
        char_sequence: str,
        max_depth: int = 2,
        using_dir_pad: bool = False,
        current_position: tuple[int, int] = None
) -> int:
    """
    Recursively compute the total button presses needed to follow a sequence of keypad inputs.

    Args:
        char_sequence: The string of keypad characters to process.
        max_depth: Recursion depth controlling how many times we can permute directions
                   on the directional keypad.
        using_dir_pad: Whether we are referencing the directional keypad or the numeric one.
        current_position: The (x, y) position where we currently are on the keypad.

    Returns:
        The number of button presses required to move through char_sequence,
        plus any required 'A' presses.
    """
    # Choose the appropriate keypad map
    keypad_map = DIR_KEYPAD if using_dir_pad else NUM_KEYPAD

    # If nothing left to process, no presses are needed.
    if not char_sequence:
        return 0

    # If no current position is provided, start from the button 'A'
    if current_position is None:
        current_position = keypad_map['A']

    # The next character we need to press
    next_char = char_sequence[0]

    curr_x, curr_y = current_position
    tgt_x, tgt_y = keypad_map[next_char]

    # Determine how many steps horizontally/vertically
    dx = tgt_x - curr_x
    dy = tgt_y - curr_y

    # Build a string of movement directions (e.g., ">>vv")
    move_sequence = ''
    if dx > 0:
        move_sequence += '>' * dx
    elif dx < 0:
        move_sequence += '<' * (-dx)
    if dy > 0:
        move_sequence += 'v' * dy
    elif dy < 0:
        move_sequence += '^' * (-dy)

    # If we still have recursion depth to use, try permutations of the movement sequence
    if max_depth > 0:
        candidate_lengths = []
        # We test each unique permutation of move_sequence
        for perm in set(permutations(move_sequence)):
            test_x, test_y = curr_x, curr_y
            valid_path = True

            # Check if each step in perm remains on the keypad
            for direction_char in perm:
                mx, my = MOVES[direction_char]
                test_x += mx
                test_y += my

                # If we leave the keypad, this permutation fails
                if (test_x, test_y) not in keypad_map.values():
                    valid_path = False
                    break

            # If it never broke (still valid), compute cost of pressing those + 'A'
            if valid_path:
                # Add an 'A' press at the end
                new_seq = perm + ('A',)
                candidate_lengths.append(
                    compute_keypresses(
                        ''.join(new_seq),
                        max_depth - 1,
                        True
                    )
                )

        # Among all valid permutations, take the minimal cost
        move_cost = min(candidate_lengths)
    else:
        # If we can't permute any further, it's just the length of the move plus pressing 'A'
        move_cost = len(move_sequence) + 1

    # Move on to the next character in char_sequence, adding the cost so far
    return move_cost + compute_keypresses(
        char_sequence[1:],
        max_depth,
        using_dir_pad,
        (tgt_x, tgt_y)
    )


def solve(lines: list[str]) -> None:
    """
    Compute and print results for part 1 and part 2 based on the given input lines.
    """
    part1_result = 0
    part2_result = 0

    for instruction in lines:
        # All but the last character to int (equivalent to int(instruction[:-1]))
        num_value = int(instruction[:-1])

        # Part 1 uses default depth=2
        part1_keypresses = compute_keypresses(instruction)
        part1_result += num_value * part1_keypresses

        # Part 2 uses depth=25
        part2_keypresses = compute_keypresses(instruction, max_depth=25)
        part2_result += num_value * part2_keypresses

    print(f"Part 1 result: {part1_result}")
    print(f"Part 2 result: {part2_result}")


if __name__ == '__main__':
    instructions = parse_input("2024_day21_input.txt")
    solve(instructions)
