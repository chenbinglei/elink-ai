Object.defineProperty(window, "siteConfig", {
  value: {
    // iemsUrl: "https://derms.enlinkitech.com",
    iemsUrl: window.__APP_CONFIG__?.iemsUrl || "",
   
  },
  writable: false,
  configurable: false,
  enumerable: false,
});
