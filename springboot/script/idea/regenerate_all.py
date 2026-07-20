#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Regenerate all HTTP test files with correct UTF-8 BOM encoding
"""

import os
import re
import glob

controller_dir = r"f:\project\kaiyuan\fastbee-dev\springboot\fastbee-open-api\src\main\java\com\fastbee\controller"
output_base = r"f:\project\kaiyuan\fastbee-dev\springboot\script\idea"

generated = 0

# Find all controller files
for root, dirs, files in os.walk(controller_dir):
    for filename in files:
        if not filename.endswith('Controller.java'):
            continue
            
        controller_path = os.path.join(root, filename)
        controller_name = filename.replace('.java', '')
        
        # Read controller file
        with open(controller_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Extract base path from @RequestMapping
        mapping_match = re.search(r'@RequestMapping\("([^"]+)"\)', content)
        base_path = mapping_match.group(1) if mapping_match else '/unknown'
        
        # Extract all mappings
        get_mappings = re.findall(r'@GetMapping\("([^"]*)"\)', content)
        post_mappings = re.findall(r'@PostMapping\("([^"]*)"\)', content)
        put_mappings = re.findall(r'@PutMapping\("([^"]*)"\)', content)
        delete_mappings = re.findall(r'@DeleteMapping\("([^"]*)"\)', content)
        
        # Determine target directory
        rel_dir = os.path.relpath(root, controller_dir)
        target_dir = os.path.join(output_base, rel_dir)
        os.makedirs(target_dir, exist_ok=True)
        
        output_file = os.path.join(target_dir, f"{controller_name}.http")
        
        # Generate test content with proper Chinese
        lines = []
        
        # GET requests
        lines.append(f"### 查询{controller_name}分页列表 => 成功")
        lines.append(f"GET {{{{baseUrl}}}}{base_path}/list?pageNum=1&pageSize=10")
        lines.append("Authorization: Bearer {{token}}")
        lines.append("")
        
        for path in get_mappings:
            if not path or path in ['list', 'export']:
                continue
            
            lines.append(f"### 查询{path} => 成功")
            if path.startswith('/'):
                lines.append(f"GET {{{{baseUrl}}}}{base_path}{path}")
            else:
                lines.append(f"GET {{{{baseUrl}}}}{base_path}/{path}")
            lines.append("Authorization: Bearer {{token}}")
            lines.append("")
        
        # POST requests
        lines.append(f"### 新增{controller_name} => 成功")
        lines.append(f"POST {{{{baseUrl}}}}{base_path}")
        lines.append("Authorization: Bearer {{token}}")
        lines.append("Content-Type: application/json")
        lines.append("")
        lines.append("{")
        lines.append('  "name": "测试数据"')
        lines.append("}")
        lines.append("")
        
        for path in post_mappings:
            if not path or path == 'export':
                continue
            
            lines.append(f"### 执行{path} => 成功")
            if path.startswith('/'):
                lines.append(f"POST {{{{baseUrl}}}}{base_path}{path}")
            else:
                lines.append(f"POST {{{{baseUrl}}}}{base_path}/{path}")
            lines.append("Authorization: Bearer {{token}}")
            lines.append("Content-Type: application/json")
            lines.append("")
            lines.append("{")
            lines.append('  "key": "value"')
            lines.append("}")
            lines.append("")
        
        # PUT requests
        lines.append(f"### 修改{controller_name} => 成功")
        lines.append(f"PUT {{{{baseUrl}}}}{base_path}")
        lines.append("Authorization: Bearer {{token}}")
        lines.append("Content-Type: application/json")
        lines.append("")
        lines.append("{")
        lines.append('  "id": 1,')
        lines.append('  "name": "更新后的名称"')
        lines.append("}")
        lines.append("")
        
        for path in put_mappings:
            if not path:
                continue
            
            lines.append(f"### 执行{path} => 成功")
            if path.startswith('/'):
                lines.append(f"PUT {{{{baseUrl}}}}{base_path}{path}")
            else:
                lines.append(f"PUT {{{{baseUrl}}}}{base_path}/{path}")
            lines.append("Authorization: Bearer {{token}}")
            lines.append("Content-Type: application/json")
            lines.append("")
            lines.append("{")
            lines.append('  "id": 1')
            lines.append("}")
            lines.append("")
        
        # DELETE requests
        lines.append(f"### 删除{controller_name} => 成功")
        lines.append(f"DELETE {{{{baseUrl}}}}{base_path}/1,2,3")
        lines.append("Authorization: Bearer {{token}}")
        lines.append("")
        
        for path in delete_mappings:
            if not path:
                continue
            
            lines.append(f"### 执行删除{path} => 成功")
            if path.startswith('/'):
                lines.append(f"DELETE {{{{baseUrl}}}}{base_path}{path}")
            else:
                lines.append(f"DELETE {{{{baseUrl}}}}{base_path}/{path}")
            lines.append("Authorization: Bearer {{token}}")
            lines.append("")
        
        # Write file with UTF-8 BOM
        content = '\n'.join(lines)
        with open(output_file, 'w', encoding='utf-8-sig') as f:
            f.write(content)
        
        generated += 1
        print(f"Generated: {controller_name}.http")

print(f"\n========================================")
print(f"Generation complete! Total: {generated} files")
print(f"========================================")
