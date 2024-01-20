import flask
from flask import Flask, request, redirect, render_template
import os
import fnmatch
from markupsafe import escape
from flask_sqlalchemy import SQLAlchemy
# import mysql.connector
import pymysql
import string
import random

app = flask.Flask(__name__)

IMAGE_FOLDER = './Image/'
# IMAGE_FOLDER = os.path.dirname('Image/')
app.config['DEBUG'] = True
app.config['IMAGE_FOLDER'] = IMAGE_FOLDERapp = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root@0.0.0.0:3306/test'
db = SQLAlchemy(app)


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)

    def __repr__(self):
        return '<User %r>' % self.username

def display_files():
    list_file = []
    for filename in os.listdir(IMAGE_FOLDER):
        list_file.append(filename)
    return list_file

@app.route('/create', methods=['GET'])
def create():
    db.create_all()
    return '<h1>Hello Create</h1>'

@app.route('/insert', methods=['GET'])
def insert():
    number_of_strings = 5
    length_of_string = 8
    for x in range(number_of_strings):

        user = User(username=''.join(random.choice(string.ascii_letters + string.digits) for _ in range(length_of_string)), email=''.join(random.choice(string.ascii_letters + string.digits) for _ in range(length_of_string)) + '@example.com')
        db.session.add(user)
        db.session.commit()
        print(''.join(random.choice(string.ascii_letters + string.digits) for _ in range(length_of_string)))
    # admin = User(username='admin', email='admin@example.com')
    # guest = User(username='guest', email='guest@example.com')
    # db.session.add(admin)
    # db.session.add(guest)
    # db.session.commit()
    return '<h1>Hello Insert</h1>'

@app.route('/delete', methods=['GET'])
def delete():
    # 查询要删除的数据
    user_to_delete = User.query.filter_by(username='admin').first()
    # 检查数据是否存在
    if user_to_delete:
        # 删除数据
        db.session.delete(user_to_delete)
        db.session.commit()
    else:
        print('User 'admin' not found, cannot delete.')
    return '<h1>Hello Delete</h1>'

@app.route('/list_tabel', methods=['GET'])
def list_tabel():
    # 查询并列出所有数据
    all_users = User.query.all()
    # 遍历所有数据并打印出来
    # for user in all_users:
    #     print(f'ID: {user.id}, Username: {user.username}, Email: {user.email}')
    return render_template('user_list.html', users=all_users)

@app.route('/', methods=['GET'])
def home():
    return '<h1>Hello Flask!</h1>'


# https://stackoverflow.com/questions/44926465/upload-image-in-flask
@app.route('/uploadImage', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        if 'file' not in request.files:
            return 'there is no file in form!'
        file = request.files['file']
        path = os.path.join(app.config['IMAGE_FOLDER'], file.filename)
        file.save(path)
        return redirect('/uploadImage')

    view = f'''
    <h1>Upload new File</h1>
    <form method='post' enctype='multipart/form-data'>
      <input type='file' name='file'>
      <input type='submit'>
    </form>
    <hr>
    <h1>View File</h1>
    <form method='post' enctype='multipart/form-data'>
        <table>
    '''
    list_file = display_files()
    
    for filename in list_file:
      td = f'''        
      <tr>
        <td><a href='/viewImage/{filename}'>{filename}</a></td>
        <td><a style='color:red;text-decoration:none;' href='/delete/{filename}' id='remove' >刪除</a></td>
      </tr>
      '''
      view = view + td
    
    end = f'''
        </ul>
    </form>
    '''  
    view = view + end
    return view

# @app.route('/viewdImage', methods=['GET'])
# def view_file():
#     list_file = display_files()
    
#     file_list_str = f'''
#     <h1>View File</h1>
#     <form method='post' enctype='multipart/form-data'>
#         <table>'''
        
#     for filename in list_file:
#         li = f'''        
#       <tr>
#         <td><a href='/viewImage/{filename}'>{filename}</a></td>
#         <td><a style='color:red;text-decoration:none;' href='/delete/{filename}' id='remove' >刪除</a></td>
#       </tr>
#       '''

#         file_list_str = file_list_str + li
#     end = f'''
#         </ul>
#     </form>
#     '''  
#     file_list_str = file_list_str + end

#     return file_list_str

@app.route('/delete/<filename>', methods=['GET'])
def delete_file(filename):
    os.remove(IMAGE_FOLDER + filename)
    return redirect('/uploadImage')


def return_img_stream(img_local_path):
    import base64
    img_stream = ''
    with open(img_local_path, 'rb') as img_f:
        img_stream = img_f.read()
        img_stream = base64.b64encode(img_stream).decode()
    return img_stream

# 跳转到html页面显示图片   app.route()为跳转路由，类似springboot 
@app.route('/viewImage/<filename>', methods=['GET']) 
def view_image(filename):
  img_stream = return_img_stream(IMAGE_FOLDER + filename)
  return render_template('view_image.html', img_stream=img_stream)

# app.run()
app.run(host='0.0.0.0', port=80)
