/**
 * Event Management System - Client Side JavaScript
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Auto dismiss alert messages after 6 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            try {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            } catch (e) {
                alert.style.display = 'none';
            }
        }, 6000);
    });

    // 2. Table Search Filter (Used in Participant Management)
    const tableSearchInput = document.getElementById('tableSearchInput');
    if (tableSearchInput) {
        tableSearchInput.addEventListener('keyup', function () {
            const filter = this.value.toLowerCase();
            const rows = document.querySelectorAll('#participantsTable tbody tr');

            rows.forEach(function (row) {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(filter) ? '' : 'none';
            });
        });
    }
});

// Copy ticket code to clipboard helper
function copyTicketCode(code) {
    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(code).then(() => {
            alert('Ticket code copied to clipboard: ' + code);
        });
    } else {
        const tempInput = document.createElement('input');
        tempInput.value = code;
        document.body.appendChild(tempInput);
        tempInput.select();
        document.execCommand('copy');
        document.body.removeChild(tempInput);
        alert('Ticket code copied to clipboard: ' + code);
    }
}
