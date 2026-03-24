import os

menu_update_path = r"E:\WebStormProjects\RealBigProject2\ml-web\src\views\ums\menu\MenuUpdate.vue"
comment_path = r"E:\WebStormProjects\RealBigProject2\ml-web\src\views\cms\comment\Comment.vue"

def fix_menu_update():
    print(f"Processing {menu_update_path}...")
    try:
        with open(menu_update_path, 'r', encoding='utf-8') as f:
            content = f.read()

        # 1. Add URL validation rule
        if "url: RULE.MENU_URL" not in content:
            target_rules = "let rules = {title: RULE.TITLE, info: RULE.INFO};"
            replacement_rules = "let rules = {title: RULE.TITLE, info: RULE.INFO, url: RULE.MENU_URL};"
            content = content.replace(target_rules, replacement_rules)

        # 2. Ensure url is initialized if missing
        if "if (!menu.url) menu.url = '/';" not in content:
            target_init = "let menu = JSON.parse(sessionStorage.getItem('row'));"
            replacement_init = "let menu = JSON.parse(sessionStorage.getItem('row'));\nif (!menu.url) menu.url = '/'; // Ensure URL has a default valid value"
            content = content.replace(target_init, replacement_init)

        with open(menu_update_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print("Successfully updated MenuUpdate.vue rules and init.")
    except Exception as e:
        print(f"Error: {e}")


def fix_comment():
    print(f"Processing {comment_path}...")
    try:
        with open(comment_path, 'r', encoding='utf-8') as f:
            content = f.read()

        # Target line that was previously modified
        target_line = "Object.values(records.value).forEach(comment => { if (comment['episode']) comment['episode']['title'] = `${comment['episode']['title']}【${comment['episode']['id']}】`; });"
        
        # New safe line that provides a default empty episode object to prevent template rendering errors
        new_safe_line = """Object.values(records.value).forEach(comment => { 
      if (comment['episode']) {
        comment['episode']['title'] = `${comment['episode']['title']}【${comment['episode']['id']}】`; 
      } else {
        comment['episode'] = { title: '视频已删除' }; // Provide default to prevent template error
      }
    });"""

        if target_line in content:
            content = content.replace(target_line, new_safe_line)
            with open(comment_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print("Successfully updated Comment.vue with fallback object.")
        else:
            print("Target line not found in Comment.vue, it might have been changed already.")
            
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    fix_menu_update()
    fix_comment()
