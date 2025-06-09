import json

# firebase firestore:indexes --json > firestore_indexes_current.json


# run: ./check_difference_indexes.py

def load_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            return json.load(f)
    except UnicodeDecodeError:
        try:
            with open(filepath, 'r', encoding='utf-8-sig') as f:
                return json.load(f)
        except UnicodeDecodeError:
            with open(filepath, 'r', encoding='utf-16') as f:
                return json.load(f)

def index_to_str(index):
    fields = index.get("fields", [])
    sorted_fields = sorted(fields, key=lambda x: x.get("fieldPath", ""))
    fields_str = ",".join(f"{f.get('fieldPath')}:{f.get('order', '')}{f.get('arrayConfig', '')}" for f in sorted_fields)
    return f"collectionGroup:{index.get('collectionGroup')}|fields:{fields_str}"

def diff_indexes(current_file, generated_file, diff_file):
    current = load_file(current_file)
    generated = load_file(generated_file)


    indexes1 = current.get("indexes", [])
    indexes2 = generated.get("indexes", [])

    set1 = set(index_to_str(idx) for idx in indexes1)
    set2 = set(index_to_str(idx) for idx in indexes2)


    diff_1_2 = set1 - set2
    diff_2_1 = set2 - set1

    differences = {
        "in_" + current_file: list(diff_1_2),
        "in_" + generated_file: list(diff_2_1),
    }

    with open(diff_file, 'w') as f:
        json.dump(differences, f, indent=2)

    print(f"Differences saved to {diff}")


diff_indexes("./firestore_indexes_current.json", "./firestore_indexes_generated.json", "./firestore_indexes_diff.json")
