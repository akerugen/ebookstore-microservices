export function decodeJwtPayload(token) {
  if (!token) {
    return null;
  }
  try {
    const parts = token.split(".");
    if (parts.length !== 3) {
      return null;
    }
    const payload = parts[1];
    const decoded = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    const json = decodeURIComponent(
      decoded
        .split("")
        .map((c) => {
          return `%${`00${c.charCodeAt(0).toString(16)}`.slice(-2)}`;
        })
        .join("")
    );
    return JSON.parse(json);
  } catch (e) {
    return null;
  }
}

export function hasRole(userInfo, roles) {
  if (!userInfo || !userInfo.role) {
    return false;
  }
  return roles.includes(userInfo.role);
}



