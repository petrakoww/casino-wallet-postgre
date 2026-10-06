const crypto = require("crypto");

// same as application.yml
const secret = "development-secret-change-me";

const payload = {
	depositId: "97097a72-25ce-4533-9ffd-8bc5d48884e7",
	providerReference: "provider-payment-manual-deposit-001",
	amountCents: 5000,
};

// !IMPORTANT GET THE RAW BODY STRINGIFIED, NOT THE OBJECT
const rawBody = JSON.stringify(payload);

const signature = crypto
	.createHmac("sha256", secret)
	.update(rawBody, "utf8")
	.digest("hex");

console.log("RAW BODY:");
console.log(rawBody);

console.log("\nSIGNATURE:");
console.log(signature);

console.log("\nCURL:");
console.log(`
curl -X POST http://localhost:8080/api/payments/callback \\
  -H "Content-Type: application/json" \\
  -H "X-Signature: ${signature}" \\
  -d '${rawBody}'
`);