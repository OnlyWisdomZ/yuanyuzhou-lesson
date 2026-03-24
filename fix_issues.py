import os

# Paths
menu_update_path = r"E:\WebStormProjects\RealBigProject2\ml-web\src\views\ums\menu\MenuUpdate.vue"
comment_path = r"E:\WebStormProjects\RealBigProject2\ml-web\src\views\cms\comment\Comment.vue"

def fix_menu_update():
    print(f"Processing {menu_update_path}...")
    try:
        if not os.path.exists(menu_update_path):
            print(f"File not found: {menu_update_path}")
            return

        with open(menu_update_path, 'r', encoding='utf-8') as f:
            content = f.read()

        # Check if url field is already there
        if "prop: 'url'" in content:
            print("MenuUpdate.vue already contains 'url' field.")
            return

        # Target string to replace (inserting after idx)
        target = "{label: '序号', prop: 'idx', type: 'number', required: true, span: 12},"
        replacement = "{label: '序号', prop: 'idx', type: 'number', required: true, span: 12},\n  {label: '地址', prop: 'url', required: true, span: 24},"

        if target in content:
            new_content = content.replace(target, replacement)
            with open(menu_update_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print("Successfully patched MenuUpdate.vue")
        else:
            print("Target string not found in MenuUpdate.vue")

    except Exception as e:
        print(f"Error processing MenuUpdate.vue: {e}")

def fix_comment():
    print(f"Processing {comment_path}...")
    try:
        if not os.path.exists(comment_path):
            print(f"File not found: {comment_path}")
            return

        with open(comment_path, 'r', encoding='utf-8') as f:
            content = f.read()

        # Target unsafe line
        unsafe_line = "Object.values(records.value).forEach(comment => comment['episode']['title'] = `${comment['episode']['title']}【${comment['episode']['id']}】`);"
        
        # Safe replacement
        safe_line = "Object.values(records.value).forEach(comment => { if (comment['episode']) comment['episode']['title'] = `${comment['episode']['title']}【${comment['episode']['id']}】`; });"

        if unsafe_line in content:
            new_content = content.replace(unsafe_line, safe_line)
            with open(comment_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print("Successfully patched Comment.vue")
        elif safe_line in content:
             print("Comment.vue is already patched.")
        else:
            print("Target unsafe line not found in Comment.vue")
            # Fallback for spacing variations? 
            # Let's try to match a simpler part if exact match fails, or just report failure.
            # The provided file content showed exact match.

    except Exception as e:
        print(f"Error processing Comment.vue: {e}")

if __name__ == "__main__":
    fix_menu_update()
    fix_comment()
