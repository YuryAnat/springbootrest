let $userModal = $('#user_modal');

$(document).ready(function () {
    loadUsers();
});

$userModal.on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var user_id = button.data('id');
    var user_action = button.data('action');
    const user = setUserModalForm(user_id);
    let $modalButtonAction = $('#modal_button_action');
    if (user_action.indexOf('edit') > -1) {
        $modalButtonAction.text('Edit');
        $modalButtonAction.removeClass('btn-danger');
        $modalButtonAction.addClass('btn btn-primary');
        $modalButtonAction.removeAttr('onclick');
        $modalButtonAction.attr('onClick', 'sendEditUser()');
        $('#userForm :input').prop('disabled', false);
    } else {
        $modalButtonAction.text('Delete');
        $modalButtonAction.removeClass('btn-primary');
        $modalButtonAction.addClass('btn btn-danger');
        $modalButtonAction.removeAttr('onclick');
        $modalButtonAction.attr('onClick', 'sendDeleteUser()');
        $('#userForm :input').prop('disabled', true);
    }
});

async function sendEditUser() {
    let id = $('#modal_id').val();
    let formInput = $("#userForm").serializeArray();
    let data = {};
    let roles = [];

    $(formInput).each(function (index, obj) {
        if (obj.name === 'roles') {
            roles.push(obj.value);
            data['roles'] = roles;
        } else {
            data[obj.name] = obj.value;
        }
    });

    let userInput = JSON.stringify(data);
    let response = await fetch('/admin/rest/editUser', {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/json'
        },
        body: userInput
    });

    if (response.status === 200){
        response.json().then(user => {
            let userRow = userToRow(user);
            $('tr[id="' + id + '"]').replaceWith(userRow);
        })
    } else {
        alert(response.error());
    }
    $userModal.modal('hide');
}

async function sendNewUser() {
    var formInput = $("#newUser :input").serializeArray();
    var data = {};
    var roles = [];

    $(formInput).each(function (index, obj) {
        if (obj.name === 'roles') {
            roles.push(obj.value);
            data['roles'] = roles;
        } else {
            data[obj.name] = obj.value;
        }
    });

    let userInput = JSON.stringify(data);
    let response = await fetch('/admin/rest/newUser', {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/json'
        },
        body: userInput
    });

    if (response.status === 200){
        let returnUser = response.json().then(user => {
            let userRow = userToRow(user);
            $('#userTable tbody').append(userRow);
        });

        $('#adminTabs a[href="#allUsers"]').tab('show');
        clearUserForm();
    } else {
        alert(response.error());
    }

}

async function loadUsers() {
    $('#userTable tbody').empty();
    const response = await fetch('/admin/rest/users');
    const result = await response.json();
    result.forEach(user => {
        let userRow = userToRow(user)
        $('#userTable tbody').append(userRow);
    });
}

function userToRow(user) {
    return `$(<tr id="${user.id}">
                        <th scope="row">${user.id}</th>
                        <td>${user.name}</td>
                        <td>${user.lastName}</td>
                        <td>${user.email}</td>
                        <td>
                            <ul>
                                <li>${user.rolesNames}</li>
                            </ul>
                        </td>
                        <td>
                            <button class="btn btn-info btn-sm" data-toggle="modal" data-id="${user.id}" data-target="#user_modal" data-action="editUser">Edit</button>
                        </td>
                        <td>
                            <button class="btn btn-danger btn-sm" data-toggle="modal" data-id="${user.id}" data-target="#user_modal" data-action="deleteUser">Delete</button>
                        </td>
                    </tr>)`;
}

function sendDeleteUser() {
    let id = $('#modal_id').val();
    fetch('/admin/rest/deleteUser/' + id).then(function (respone) {
        if (respone.status === 200){
            $('tr[id="' + id + '"]').remove();
            $userModal.modal('hide');
        }else {
            alert(respone.error());
        }
    });
}

function setUserModalForm(id) {
    fetch('/admin/rest/user/' + id)
        .then(function (response) {
            return response.json();
        }).then(function (user) {
        $('#modal_id').val(user.id);
        $('#modal_name').val(user.name);
        $('#modal_lname').val(user.lastName);
        $('#modal_email').val(user.email);
        for (var role of user.roles) {
            $('#role_' + role.id).prop('selected', true);
        }
    });
}

function clearUserForm() {
    let $newUser = $('#newUser');
    $newUser.find("input").val('');
    $newUser.find("option:selected").prop('selected', false);
}
