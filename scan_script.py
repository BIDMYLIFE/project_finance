import re, os, sys  
patterns = [  
    re.compile(r"JWT_SECRET", re.IGNORECASE),  
    re.compile(r"DB_PASSWORD", re.IGNORECASE),  
    re.compile(r"password\s*[:=]\s*(1111|changeit)", re.IGNORECASE),  
    re.compile(r"stacktrace", re.IGNORECASE),  
    re.compile(r"printStackTrace", re.IGNORECASE),  
    re.compile(r"refreshToken", re.IGNORECASE),  
    re.compile(r"accessToken", re.IGNORECASE)  
]  
dirs_to_check = ["src/main", "src/test", "docs", "openspec/changes/auth-jwt-admin-bootstrap"]  
for base in dirs_to_check:  
    if not os.path.exists(base): continue  
    if os.path.isfile(base):  
        with open(base, "r", encoding="utf-8", errors="ignore") as f:  
            for idx, line in enumerate(f, 1):  
                for p in patterns:  
                    if p.search(line):  
                        print(f"{base}:{idx}:{line.strip()}")  
                        break  
    else:  
        for root, dirs, files in os.walk(base):  
            for file in files:  
                filepath = os.path.join(root, file)  
                try:  
                    with open(filepath, "r", encoding="utf-8", errors="ignore") as f:  
                        for idx, line in enumerate(f, 1):  
                            for p in patterns:  
                                if p.search(line):  
                                    print(f"{filepath}:{idx}:{line.strip()}")  
                                    break  
                except Exception as e: pass 
